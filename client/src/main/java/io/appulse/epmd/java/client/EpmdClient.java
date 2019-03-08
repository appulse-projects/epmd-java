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

package io.appulse.epmd.java.client;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

import java.io.Closeable;
import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import io.appulse.epmd.java.client.exception.EpmdRegistrationException;
import io.appulse.epmd.java.client.exception.EpmdRegistrationNameConflictException;
import io.appulse.epmd.java.core.model.request.GetEpmdDump;
import io.appulse.epmd.java.core.model.request.GetEpmdInfo;
import io.appulse.epmd.java.core.model.request.GetNodeInfo;
import io.appulse.epmd.java.core.model.request.Kill;
import io.appulse.epmd.java.core.model.request.Registration;
import io.appulse.epmd.java.core.model.request.Stop;
import io.appulse.epmd.java.core.model.response.EpmdDump;
import io.appulse.epmd.java.core.model.response.EpmdInfo.NodeDescription;
import io.appulse.epmd.java.core.model.response.NodeInfo;
import io.appulse.epmd.java.core.model.response.RegistrationResult;
import io.appulse.utils.threads.AppulseExecutors;
import io.appulse.utils.threads.AppulseThreadFactory;
import io.appulse.utils.threads.FutureUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * EPMD client.
 *
 * @since 0.2.2
 * @author Artem Labazin
 */
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class EpmdClient implements Closeable {

  Set<String> registered = ConcurrentHashMap.<String>newKeySet();

  ExecutorService executor;

  @Getter
  InetAddress address;

  @Getter
  Integer port;

  /**
   * Default no arguments constructor.
   * <p>
   * It uses default inet address (localhost) and port (4369).
   */
  public EpmdClient () {
    this(EpmdDefaults.ADDRESS, EpmdDefaults.PORT);
  }

  /**
   * Constructs EPMD client with the specified {@link InetAddress}.
   * <p>
   * It uses default port (4369).
   *
   * @param address EPMD server address
   */
  public EpmdClient (InetAddress address) {
    this(address, EpmdDefaults.PORT);
  }

  /**
   * Constructs EPMD client with the specified port.
   * <p>
   * It uses default inet address (localhost).
   *
   * @param port EPMD server port
   */
  public EpmdClient (int port) {
    this(EpmdDefaults.ADDRESS, port);
  }

  /**
   * Constructs EPMD client with the specified address and port.
   *
   * @param address EPMD server address
   *
   * @param port EPMD server port
   */
  @Builder
  public EpmdClient (@NonNull InetAddress address, int port) {
    this.address = address;
    this.port = port;

    executor = AppulseExecutors.newCachedThreadPool()
        .corePoolSize(2)
        .maxPoolSize(2)
        .enableClientTrace()
        .threadFactory(AppulseThreadFactory.builder()
            .name("epmd-%d")
            .build())
        .keepAliveTime(500L)
        .unit(MILLISECONDS)
        .queueLimit(100)
        .build();
  }

  /**
   * Registers node at EPMD server.
   *
   * @param request registration holder
   *
   * @return creation id from EPMD
   */
  public CompletableFuture<RegistrationResult> register (@NonNull Registration request) {
    if (registered.contains(request.getName())) {
      val exception = new EpmdRegistrationNameConflictException(request.getName());
      log.error(exception.getMessage(), exception);
      return FutureUtils.completedExceptionally(exception);
    }

    val supplier = CommandRegistration.builder()
        .address(address)
        .port(port)
        .request(request)
        .build();

    return CompletableFuture.supplyAsync(supplier, executor)
        .exceptionally(throwable -> {
          log.error("'{}' wasn't registered successfully", request.getName());
          throw new EpmdRegistrationException(throwable);
        })
        .thenApply(result -> {
          registered.add(request.getName());
          log.info("'{}' was registered successfully", request.getName());
          return result;
        });
  }

  /**
   * Returns nodes infos from EPMD.
   *
   * @return list of nodes from EPMD
   */
  public CompletableFuture<List<EpmdDump.NodeDump>> dump () {
    CommandDump command = CommandDump.builder()
        .address(address)
        .port(port)
        .request(new GetEpmdDump())
        .build();

    return CompletableFuture.supplyAsync(command, executor);
  }

  /**
   * Stops a node by name.
   *
   * @param node node's name
   *
   * @return {@link CompletableFuture} instance of success or not action
   */
  public CompletableFuture<Void> stop (@NonNull String node) {
    val supplier = CommandStop.builder()
        .address(address)
        .port(port)
        .request(new Stop(node))
        .build();

    return CompletableFuture.supplyAsync(supplier, executor);
  }

  /**
   * This request kills the running EPMD server.
   *
   * @return was it killed or not.
   */
  public CompletableFuture<Boolean> kill () {
    val supplier = CommandKill.builder()
        .address(address)
        .port(port)
        .request(new Kill())
        .build();

    return CompletableFuture.supplyAsync(supplier, executor);
  }

  /**
   * Look up a specific node in lremote or ocal EPMD server.
   *
   * @param node a full or short node name to search
   *
   * @return an optional information about a node from EPMD server
   */
  public CompletableFuture<Optional<NodeInfo>> lookup (@NonNull String node) {
    return lookup(node, EpmdDefaults.PORT);
  }

  /**
   * Look up a specific node in lremote or ocal EPMD server.
   *
   * @param node a full or short node name to search
   *
   * @param nodePort remote (or local) EPMD server's port
   *
   * @return an optional information about a node from EPMD server
   */
  @SneakyThrows
  public CompletableFuture<Optional<NodeInfo>> lookup (@NonNull String node, int nodePort) {
    val tokens = node.split("@", 2);
    val shortName = tokens[0];
    val nodeAddress = tokens.length == 2
                      ? InetAddress.getByName(tokens[1])
                      : EpmdDefaults.ADDRESS;

    return lookup(shortName, nodeAddress, nodePort);
  }

  /**
   * Look up a specific node in lremote or ocal EPMD server.
   *
   * @param node a full or short node name to search
   *
   * @param nodeAddress remote (or local) EPMD server's inet address
   *
   * @return an optional information about a node from EPMD server
   */
  public CompletableFuture<Optional<NodeInfo>> lookup (String node, InetAddress nodeAddress) {
    return lookup(node, nodeAddress, EpmdDefaults.PORT);
  }

  /**
   * Look up a specific node in lremote or ocal EPMD server.
   *
   * @param node a full or short node name to search
   *
   * @param nodeAddress a remote (or local) EPMD server's inet address
   *
   * @param nodePort a remote (or local) EPMD server's port
   *
   * @return an optional information about a node from EPMD server
   */
  public CompletableFuture<Optional<NodeInfo>> lookup (@NonNull String node, @NonNull InetAddress nodeAddress, int nodePort) {
    val tokens = node.split("@", 2);
    val shortName = tokens[0];
    log.debug("Looking up node '{}' at '{}:{}'", shortName, nodeAddress, nodePort);

    val supplier = CommandGetNodeInfo.builder()
        .address(nodeAddress)
        .port(nodePort)
        .request(new GetNodeInfo(shortName))
        .build();

    return CompletableFuture.supplyAsync(supplier, executor);
  }

  /**
   * Returns all registered nodes descriptions in a local EPMD server.
   *
   * @return a list of all registered nodes.
   */
  public CompletableFuture<List<NodeDescription>> getNodes () {
    return getNodes(EpmdDefaults.ADDRESS, EpmdDefaults.PORT);
  }

  /**
   * Returns all registered nodes descriptions in a local EPMD server.
   *
   * @param nodePort a nonstandard EPMD server's port
   *
   * @return a list of all registered nodes.
   */
  public CompletableFuture<List<NodeDescription>> getNodes (int nodePort) {
    return getNodes(EpmdDefaults.ADDRESS, nodePort);
  }

  /**
   * Returns all registered nodes descriptions in a remote EPMD server.
   *
   * @param host a remote EPMD server's host
   *
   * @return a list of all registered nodes.
   */
  public CompletableFuture<List<NodeDescription>> getNodes (@NonNull String host) {
    return getNodes(host, EpmdDefaults.PORT);
  }

  /**
   * Returns all registered nodes descriptions in a remote EPMD server.
   *
   * @param host a remote EPMD server's host
   *
   * @param nodePort a nonstandard EPMD server's port
   *
   * @return a list of all registered nodes.
   */
  @SneakyThrows
  public CompletableFuture<List<NodeDescription>> getNodes (@NonNull String host, int nodePort) {
    val nodeAddress = InetAddress.getByName(host);
    return getNodes(nodeAddress, nodePort);
  }

  /**
   * Returns all registered nodes descriptions in a remote EPMD server.
   *
   * @param nodeAddress a remote EPMD server's address
   *
   * @return a list of all registered nodes.
   */
  public CompletableFuture<List<NodeDescription>> getNodes (InetAddress nodeAddress) {
    return getNodes(nodeAddress, EpmdDefaults.PORT);
  }

  /**
   * Returns all registered nodes descriptions in a remote EPMD server.
   *
   * @param nodeAddress a remote EPMD server's address
   *
   * @param nodePort a nonstandard EPMD server's port
   *
   * @return a list of all registered nodes.
   */
  @SneakyThrows
  public CompletableFuture<List<NodeDescription>> getNodes (@NonNull InetAddress nodeAddress, int nodePort) {
    log.debug("Getting nodes from {}:{}", nodeAddress, nodePort);
    val supplier = CommandGetEpmdInfo.builder()
        .address(nodeAddress)
        .port(nodePort)
        .request(new GetEpmdInfo())
        .build();

    return CompletableFuture.supplyAsync(supplier, executor);
  }

  @Override
  @SneakyThrows
  public void close () {
    executor.shutdown();
    registered.clear();
    val terminated = executor.awaitTermination(5, SECONDS);
    log.debug("EPMD was successfully terminated - {}", terminated);
  }
}
