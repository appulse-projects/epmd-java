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

package io.appulse.epmd.java.cli;

import static ch.qos.logback.classic.Level.DEBUG;
import static java.util.Collections.singleton;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.Optional.of;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
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
import io.appulse.epmd.java.core.model.response.EpmdInfo;
import io.appulse.epmd.java.core.model.response.KillResult;
import io.appulse.epmd.java.core.model.response.NodeInfo;
import io.appulse.epmd.java.core.model.response.RegistrationResult;
import io.appulse.epmd.java.core.model.response.Response;
import io.appulse.epmd.java.core.model.response.StopResult;
import io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump;
import io.appulse.epmd.java.core.model.response.EpmdInfo.NodeDescription;
import io.appulse.utils.BytesUtils;
import io.appulse.utils.SocketUtils;
import io.appulse.utils.threads.AppulseExecutors;
import io.appulse.utils.threads.AppulseThreadFactory;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Slf4j
@Command(name = "server")
class SubcommandServer implements Runnable {

  private static final InetAddress ANY_ADDRESS;

  static {
    try {
      ANY_ADDRESS = InetAddress.getByName("0.0.0.0");
    } catch (UnknownHostException ex) {
      throw new RuntimeException(ex);
    }
  }

  @ParentCommand
  Epmd options;

  @Option(names = { "-a", "--allowed-ips" })
  Set<InetAddress> ips = singleton(ANY_ADDRESS);

  @Option(names = { "-u", "--unsafe-commands" })
  boolean unsafe;

  Map<String, Node> nodes;

  ExecutorService executor;

  @Override
  @SneakyThrows
  public void run () {
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
        } else if (!ips.contains(ANY_ADDRESS) && !ips.contains(remoteAddress)) {
          clientSocket.close();
          log.warn("unacceptable remote client's address {}", remoteAddress);
          continue;
        }
        log.debug("{} - a new incoming connection", remoteSocketAddress);

        val handler = new ServerHandler(clientSocket);
        executor.submit(handler);
      }
    } finally {
      executor.shutdown();
      val termnated = executor.awaitTermination(5, SECONDS);
      log.info("EPMD server terminated successfully ({})", termnated);
    }
  }

  private Collection<Node> getNodes () {
    nodes.entrySet()
        .removeIf(entry -> !entry.getValue().isAlive());

    return nodes.values();
  }

  private Optional<Node> getNode (String name) {
    return ofNullable(name)
        .map(nodes::get)
        .filter(Node::isAlive);
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
      } catch (Throwable ex) {
        if (options.debug) {
          log.error("handling {} connection error - '{}'", ex.getMessage(), ex);
        } else {
          log.error("handling {} connection error - '{}'", ex.getMessage());
        }
      }
    }

    private Optional<RequestProcessor<?>> findProcessor (Request request, Socket socket) {
      switch (request.getTag()) {
      case ALIVE2_REQUEST:
        return of(new RegistrationRequestProcessor(request, socket));
      case DUMP_REQUEST:
        return of(new DumpRequestProcessor(request, socket));
      case KILL_REQUEST:
        return of(new KillRequestProcessor(request, socket));
      case PORT_PLEASE2_REQUEST:
        return of(new GetNodeInfoRequestProcessor(request, socket));
      case NAMES_REQUEST:
        return of(new GetEpmdInfoRequestProcessor(request, socket));
      case STOP_REQUEST:
        return of(new StopRequestProcessor(request, socket));
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
        nodes.values().forEach(it -> {
          try {
            it.getSocket().close();
          } catch (IOException ex) {
            // noop
          }
        });
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
    public void close () {
      try {
        socket.close();
      } catch (IOException ex) {
        // noop
      }
    }

    @SneakyThrows
    boolean isAlive () {
      val nodeSocketAddress = new InetSocketAddress(socket.getInetAddress(), port);
      try (val nodeSocket = new Socket()) {
        nodeSocket.connect(nodeSocketAddress, 2_000);
        nodeSocket.close();
      } catch (Throwable ex) {
        return false;
      }
      return true;
    }
  }
}
