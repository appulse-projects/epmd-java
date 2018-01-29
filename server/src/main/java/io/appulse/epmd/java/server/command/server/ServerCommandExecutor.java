/*
 * Copyright 2018 the original author or authors.
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

package io.appulse.epmd.java.server.command.server;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import java.io.Closeable;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.epmd.java.server.command.AbstractCommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;

import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ServerCommandExecutor extends AbstractCommandExecutor implements Closeable {

  ServerCommandOptions options;

  ServerSocket serverSocket;

  ExecutorService executor;

  Map<String, Node> nodes;

  @SneakyThrows
  public ServerCommandExecutor (CommonOptions commonOptions, CommandOptions options) {
    super(commonOptions);
    this.options = ofNullable(options)
        .filter(it -> it instanceof ServerCommandOptions)
        .map(it -> (ServerCommandOptions) it)
        .orElse(new ServerCommandOptions());

    serverSocket = new ServerSocket(getPort());
    executor = Executors.newCachedThreadPool();
    nodes = new ConcurrentHashMap<>();
  }

  @Override
  @SneakyThrows
  public void execute () {
    log.debug("{}", options);
    log.info("Server command was executed");

    try {
      while (getPort() < 1) {
        val socket = serverSocket.accept();
        if (options.getAddresses().contains(socket.getInetAddress())) {
          socket.close();
          continue;
        }
        val handler = new ServerHandler(socket, nodes);
        executor.execute(handler);
      }
    } finally {
      close();
    }
  }

  @Override
  @SneakyThrows
  public void close () {
    serverSocket.close();
    if (!executor.isShutdown()) {
      executor.shutdown();
    }
    nodes.clear();
  }
}
