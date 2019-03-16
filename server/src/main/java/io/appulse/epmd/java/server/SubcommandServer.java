/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appulse.epmd.java.server;

import static ch.qos.logback.classic.Level.DEBUG;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toSet;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import io.appulse.epmd.java.core.model.NodeType;
import io.appulse.epmd.java.core.model.Protocol;
import io.appulse.epmd.java.core.model.Version;
import io.appulse.epmd.java.core.model.request.GetEpmdDump;
import io.appulse.epmd.java.core.model.request.GetEpmdInfo;
import io.appulse.epmd.java.core.model.request.GetNodeInfo;
import io.appulse.epmd.java.core.model.request.Kill;
import io.appulse.epmd.java.core.model.request.Registration;
import io.appulse.epmd.java.core.model.request.Request;
import io.appulse.epmd.java.core.model.request.Stop;
import io.appulse.epmd.java.core.model.response.EpmdDump;
import io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump;
import io.appulse.epmd.java.core.model.response.EpmdInfo;
import io.appulse.epmd.java.core.model.response.EpmdInfo.NodeDescription;
import io.appulse.epmd.java.core.model.response.KillResult;
import io.appulse.epmd.java.core.model.response.NodeInfo;
import io.appulse.epmd.java.core.model.response.RegistrationResult;
import io.appulse.epmd.java.core.model.response.Response;
import io.appulse.epmd.java.core.model.response.StopResult;
import io.appulse.utils.BytesUtils;
import io.appulse.utils.SocketUtils;
import io.appulse.utils.threads.AppulseExecutors;
import io.appulse.utils.threads.AppulseThreadFactory;

import ch.qos.logback.classic.Logger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/**
 * EPMD server command.
 */
@Slf4j
@NoArgsConstructor
@Command(
    name = "server",
    sortOptions = false,
    descriptionHeading = "%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    commandListHeading = "%nCommands:%n",
    description = "Starts the epmd server."
)
public class SubcommandServer implements Runnable {

  static final InetAddress ANY_ADDRESS;

  static final InetAddress LOOPBACK_ADDRESS;

  static final InetAddress LOCALHOST;

  static {
    try {
      ANY_ADDRESS = InetAddress.getByName("0.0.0.0");
      LOOPBACK_ADDRESS = InetAddress.getLoopbackAddress();
      LOCALHOST = InetAddress.getLocalHost();
    } catch (UnknownHostException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @ParentCommand
  Epmd options;

  @Option(
      names = { "-a", "--allowed-ips" },
      description =
          "Lets this instance of epmd listen only on the comma-separated list of IP addresses" +
          "and on the loopback address (which is implicitly added to the list if it has not been " +
          "specified). This can also be set using environment variable ERL_EPMD_ADDRESS"
  )
  Set<InetAddress> ips = new HashSet<>(asList(LOOPBACK_ADDRESS));

  @Option(
      names = { "-u", "--unsafe-commands" },
      description = {
        "Start the epmd program with relaxed command checking. This affects the following:",
        "With relaxed command checking, the epmd daemon can be killed from the localhost with i.e. " +
        "epmd -kill even if there are active nodes registered. Normally only daemons with an empty node " +
        "database can be killed with the epmd \"kill\" command.",
        "With relaxed command checking enabled, you can forcibly unregister live nodes by \"stop\" command."
      }
  )
  boolean unsafe;

  Map<String, Node> nodes;

  ExecutorService executor;

  @Builder
  SubcommandServer (Integer port,
                    @Singular Set<InetAddress> ips,
                    boolean unsafe
  ) {
    options = new Epmd();
    ofNullable(port)
        .ifPresent(options::setPort);

    this.unsafe = unsafe;

    ofNullable(ips)
        .filter(it -> !it.isEmpty())
        .map(HashSet::new)
        .ifPresent(it -> this.ips = it);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  public void run () {
    setupEnvironmentVariables();

    val root = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
    if (options.debug) {
      root.setLevel(DEBUG);
    }

    nodes = new ConcurrentHashMap<>();
    executor = AppulseExecutors.newCachedThreadPool()
        .threadFactory(AppulseThreadFactory.builder()
            .name("epmd-%d")
            .build())
        .corePoolSize(2)
        .maxPoolSize(8)
        .keepAliveTime(100L)
        .unit(MILLISECONDS)
        .queueLimit(1000)
        .build();

    try (val serverSocket = new ServerSocket(options.port, 1000)) {
      log.info("EPMD server started (debug: {}, port: {}, allowed-ips: {}, unsafe-commands: {})",
               options.debug, options.port, ips, unsafe);

      while (!Thread.interrupted()) {
        val clientSocket = serverSocket.accept();

        val remoteSocketAddress = clientSocket.getRemoteSocketAddress();
        val remoteAddress = ofNullable(remoteSocketAddress)
            .filter(it -> it instanceof InetSocketAddress)
            .map(it -> (InetSocketAddress) it)
            .map(InetSocketAddress::getAddress)
            .orElse(null);

        if (remoteAddress == null) {
          log.warn("uh?");
          continue;
        } else if (!ips.contains(ANY_ADDRESS) && !ips.contains(remoteAddress) && !LOCALHOST.equals(remoteAddress)) {
          clientSocket.close();
          log.warn("unacceptable remote client's address {}", remoteAddress);
          continue;
        }
        log.debug("{} - a new incoming connection", remoteSocketAddress);

        val handler = new ServerHandler(clientSocket);
        executor.execute(handler);
      }
    } finally {
      executor.shutdown();
      val termnated = executor.awaitTermination(5, SECONDS);
      log.info("EPMD server terminated successfully ({})", termnated);
    }
  }

  /**
   * Gets all registered and alive nodes in the server.
   *
   * @return all alive nodes
   */
  public Collection<Node> getNodes () {
    nodes.entrySet()
        .removeIf(entry -> !entry.getValue().isAlive());

    return nodes.values();
  }

  /**
   * Returns a registered node, only if it is alive.
   *
   * @param name the node's name
   *
   * @return the registered and alive node
   */
  public Optional<Node> getNode (String name) {
    return ofNullable(name)
        .map(nodes::get)
        .filter(Node::isAlive);
  }

  private void setupEnvironmentVariables () {
    if (new SubcommandServer().ips.equals(ips)) { // checks it is not set
      ips = ofNullable(System.getProperty("ERL_EPMD_ADDRESS"))
          .map(it -> it.split(","))
          .map(it -> Stream.of(it)
              .map(String::trim)
              .filter(ip -> !ip.isEmpty())
              .map(ip -> {
                try {
                  return InetAddress.getByName(ip);
                } catch (UnknownHostException ex) {
                  throw new IllegalArgumentException("Invalid host " + ip, ex);
                }
              })
              .collect(toSet()))
          .map(it -> {
            it.add(LOOPBACK_ADDRESS);
            return it;
          })
          .orElse(ips);
    }
    ips.add(LOOPBACK_ADDRESS);

    if (!unsafe) {
      val string = ofNullable(System.getProperty("ERL_EPMD_RELAXED_COMMAND_CHECK"))
          .filter(it -> !it.isEmpty())
          .orElse("true");
      unsafe = Boolean.valueOf(string);
    }
  }

  @Value
  private class ServerHandler implements Runnable {

    @NonNull
    Socket socket;

    @Override
    @SneakyThrows
    public void run () {
      try {
        val requestLengthBytes = SocketUtils.read(socket, 2);
        val requestLength = BytesUtils.asShort(requestLengthBytes);

        val requestBytes = SocketUtils.read(socket, requestLength);
        val request = Request.parse(requestBytes, requestLength);

        log.debug("the new reqeust is {}", request);
        findProcessor(request, socket)
            .map(it -> {
              log.debug("reqeust processor is {}", it.getClass().getSimpleName());
              return it;
            })
            .ifPresent(RequestProcessor::process);
      } catch (Exception ex) {
        if (options.debug) {
          log.error("handling {} connection error - '{}'", ex.getMessage(), ex);
        } else {
          log.error("handling {} connection error - '{}'", ex.getMessage());
        }
      }
    }

    private Optional<RequestProcessor<?>> findProcessor (Request request, Socket clientSocket) {
      switch (request.getTag()) {
      case ALIVE2_REQUEST:
        return of(new RegistrationRequestProcessor(request, clientSocket));
      case DUMP_REQUEST:
        return of(new DumpRequestProcessor(request, clientSocket));
      case KILL_REQUEST:
        return of(new KillRequestProcessor(request, clientSocket));
      case PORT_PLEASE2_REQUEST:
        return of(new GetNodeInfoRequestProcessor(request, clientSocket));
      case NAMES_REQUEST:
        return of(new GetEpmdInfoRequestProcessor(request, clientSocket));
      case STOP_REQUEST:
        return of(new StopRequestProcessor(request, clientSocket));
      default:
        log.warn("unsupported request's tag - {}", request.getTag());
        return empty();
      }
    }

    @RequiredArgsConstructor
    abstract class RequestProcessor<R extends Request> {

      @NonNull
      protected final R request;

      @NonNull
      protected final Socket socket;

      @SneakyThrows
      void process () {
        val response = respond();
        if (response == null) {
          socket.close();
          return;
        }

        send(response);
        afterSend(response);
      }

      protected abstract Response respond ();

      @SneakyThrows
      protected void send (Response response) {
        log.debug("sending a response to {}", socket.getRemoteSocketAddress());
        val responseBytes = response.toBytes();
        socket.getOutputStream().write(responseBytes);
        socket.getOutputStream().flush();
        log.debug("{} was sent to {}", response, socket.getRemoteSocketAddress());
      }

      @SneakyThrows
      protected void afterSend (Response response) {
        log.debug("close connection to {}", socket.getRemoteSocketAddress());
        socket.close();
      }
    }

    class RegistrationRequestProcessor extends RequestProcessor<Registration> {

      RegistrationRequestProcessor (Request request, Socket socket) {
        super((Registration) request, socket);
      }

      @Override
      protected Response respond () {
        val creation = (int) (System.currentTimeMillis() % 3 + 1);
        val node = nodes.computeIfAbsent(request.getName(), key -> Node.builder()
            .name(request.getName())
            .port(request.getPort())
            .type(request.getType())
            .protocol(request.getProtocol())
            .high(request.getHigh())
            .low(request.getLow())
            .creation(creation)
            .socket(socket)
            .build()
        );

        return RegistrationResult.builder()
            .ok(node != null)
            .creation(node == null
                      ? 0
                      : node.getCreation()
            )
            .build();
      }

      @Override
      @SneakyThrows
      protected void afterSend (Response response) {
        val result = (RegistrationResult) response;
        if (result.isOk()) {
          return;
        }
        socket.close();
      }
    }

    class DumpRequestProcessor extends RequestProcessor<GetEpmdDump> {

      DumpRequestProcessor (Request request, Socket socket) {
        super((GetEpmdDump) request, socket);
      }

      @Override
      protected Response respond () {
        val builder = EpmdDump.builder()
            .port(options.port);

        getNodes().stream()
            .map(it -> NodeDump.builder()
                .status(NodeDump.Status.ACTIVE)
                .name(it.getName())
                .port(it.getPort())
                .fileDescriptor(-1)
                .build()
            )
            .forEach(builder::node);

        return builder.build();
      }
    }

    class KillRequestProcessor extends RequestProcessor<Kill> {

      KillRequestProcessor (Request request, Socket socket) {
        super((Kill) request, socket);
      }

      @Override
      protected Response respond () {
        if (!unsafe) {
          return KillResult.NOK;
        }
        nodes.values().forEach(Node::close);
        return KillResult.OK;
      }

      @Override
      protected void afterSend (Response response) {
        if (response.equals(KillResult.NOK)) {
          return;
        }
        executor.shutdownNow();
      }
    }

    class GetNodeInfoRequestProcessor extends RequestProcessor<GetNodeInfo> {

      GetNodeInfoRequestProcessor (Request request, Socket socket) {
        super((GetNodeInfo) request, socket);
      }

      @Override
      protected Response respond () {
        return getNode(request.getName())
            .filter(Objects::nonNull)
            .map(node -> NodeInfo.builder()
                .ok(true)
                .port(node.getPort())
                .type(node.getType())
                .protocol(node.getProtocol())
                .high(node.getHigh())
                .low(node.getLow())
                .name(node.getName())
                .build())
            .orElseGet(() -> NodeInfo.builder()
                .ok(false)
                .build());
      }
    }

    class GetEpmdInfoRequestProcessor extends RequestProcessor<GetEpmdInfo> {

      GetEpmdInfoRequestProcessor (Request request, Socket socket) {
        super((GetEpmdInfo) request, socket);
      }

      @Override
      protected Response respond () {
        val builder = EpmdInfo.builder()
            .port(options.port);

        getNodes().stream()
            .map(it -> NodeDescription.builder()
                .name(it.getName())
                .port(it.getPort())
                .build()
            )
            .forEach(builder::node);

        return builder.build();
      }
    }

    class StopRequestProcessor extends RequestProcessor<Stop> {

      StopRequestProcessor (Request request, Socket socket) {
        super((Stop) request, socket);
      }

      @Override
      protected Response respond () {
        if (!unsafe) {
          return StopResult.NOEXIST;
        }

        return getNode(request.getName())
            .map(Node::getName)
            .map(nodes::remove)
            .filter(Objects::nonNull)
            .map(node -> {
              node.close();
              return StopResult.STOPPED;
            })
            .orElse(StopResult.NOEXIST);
      }
    }
  }

  @Value
  @Builder
  private static class Node implements Closeable {

    @NonNull
    String name;

    int port;

    @NonNull
    NodeType type;

    @NonNull
    Protocol protocol;

    @NonNull
    Version high;

    @NonNull
    Version low;

    int creation;

    Socket socket;

    @Override
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public void close () {
      try {
        socket.close();
      } catch (IOException ex) {
        // noop
      }
    }

    @SneakyThrows
    @SuppressFBWarnings("REC_CATCH_EXCEPTION")
    boolean isAlive () {
      val nodeSocketAddress = new InetSocketAddress(socket.getInetAddress(), port);
      try (val nodeSocket = new Socket()) {
        nodeSocket.connect(nodeSocketAddress, 2_000);
        nodeSocket.close();
      } catch (Exception ex) {
        return false;
      }
      return true;
    }
  }
}
