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

import static java.util.Collections.singleton;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.Optional.of;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.io.Closeable;
import java.io.IOException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

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
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(subcommands = {
  SubcommandKillEpmdServer.class,
  SubcommandGetAllNames.class,
  SubcommandStopNode.class
})
class CommandStartEpmdServer implements Runnable {

  @SneakyThrows
  private static Set<InetAddress> getDefaultAddresses () {
    return singleton(InetAddress.getByName("0.0.0.0"));
  }

  @Option(
    names = { "-h", "--help" },
    usageHelp = true,
    hidden = true
  )
  boolean helpRequested;

  @Option(names = "-address")
  Set<InetAddress> addresses = getDefaultAddresses();

  @Option(names = "-port")
  int port = 4369;

  @Option(names = { "-d", "-debug" })
  boolean[] debugLevel = new boolean[0];

  @Option(names = "-daemon")
  boolean daemon;

  @Option(names = "-relaxed_command_check")
  boolean checks;

  @Option(names = "-packet_timeout")
  int packetTimeout = 60;

  @Option(names = "-delay_accept")
  int delayAccept;

  @Option(names = "-delay_write")
  int delayWrite;

  Map<String, Node> nodes;

  ExecutorService executor;

  @Override
  @SneakyThrows
  public void run () {
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

    try (val serverSocket = new ServerSocket(port, 1000)) {
      while (!Thread.interrupted()) {
          val clientSocket = serverSocket.accept();

          val localAddress = clientSocket.getLocalAddress();
          if (!addresses.contains(localAddress)) {
            clientSocket.close();
            continue;
          }

          val handler = new ServerHandler(clientSocket);
          executor.submit(handler);
      }
    } finally {
      executor.shutdown();
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
      val requestLengthBytes = SocketUtils.read(socket, 2);
      val requestLength = BytesUtils.asShort(requestLengthBytes);

      val requestBytes = SocketUtils.read(socket, requestLength);
      val request = Request.parse(requestBytes);

      findProcessor(request, socket)
          .ifPresent(RequestProcessor::process);
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
        return empty();
      }
    }

    @RequiredArgsConstructor
    abstract class RequestProcessor<R extends Request> {

      @NonNull
      protected final R request;

      @NonNull
      protected final Socket socket;

      void process () {
        val response = respond();
        send(response);
        afterSend(response);
      }

      protected abstract Response respond ();

      @SneakyThrows
      protected void send (Response response) {
        val responseBytes = response.toBytes();
        socket.getOutputStream().write(responseBytes);
        socket.getOutputStream().flush();
      }

      @SneakyThrows
      protected void afterSend (Response response) {
        socket.close();
      }
    }

    class RegistrationRequestProcessor extends RequestProcessor<Registration> {

      RegistrationRequestProcessor (Request request, Socket socket) {
        super((Registration) request, socket);
      }

      @Override
      protected Response respond () {
        val creation = (int) System.currentTimeMillis() % 3 + 1;
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
            .port(port);

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
        if (!checks) {
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
            .port(port);

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
        if (!checks) {
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

    boolean isAlive () {
      if (socket.isClosed()) {
        return false;
      }
      try {
        socket.getOutputStream().write(1);
        return true;
      } catch (IOException ex) {
        // noop
      }

      try {
        socket.close();
      } catch (IOException ex) {
        // noop
      }
      return false;
    }
  }
}
