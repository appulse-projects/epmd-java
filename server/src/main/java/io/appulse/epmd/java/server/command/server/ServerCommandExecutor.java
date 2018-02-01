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

import static java.util.Optional.of;
import static lombok.AccessLevel.PRIVATE;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.epmd.java.server.command.AbstractCommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
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

  ServerSocket serverSocket;

  ExecutorService executor;

  Context context;

  @NonFinal
  volatile boolean closed;

  @SneakyThrows
  public ServerCommandExecutor (CommonOptions commonOptions, @NonNull CommandOptions options) {
    super(commonOptions);
    val serverOptions = of(options)
        .filter(it -> it instanceof ServerCommandOptions)
        .map(it -> (ServerCommandOptions) it)
        .orElse(new ServerCommandOptions());

    serverSocket = new ServerSocket(getPort());

    executor = Executors.newCachedThreadPool();

    context = Context.builder()
        .nodes(new ConcurrentHashMap<>())
        .commonOptions(commonOptions)
        .serverOptions(serverOptions)
        .build();
  }

  @Override
  @SneakyThrows
  public void execute () {
    log.debug("Server before start context: {}", context);
    try {
      while (true) {
        log.debug("Waiting new connection");

        val socket = serverSocket.accept();
        log.debug("New connection was accepted");

        if (context.getAddresses().contains(socket.getInetAddress())) {
          socket.close();
          continue;
        }

        if (isDebug()) {
          System.out.println();
        }

        val handler = new ServerWorker(socket, context);
        executor.execute(handler);
      }
    } finally {
      close();
    }
  }

  @Override
  @SneakyThrows
  public void close () {
    if (closed) {
      return;
    }
    closed = true;

    log.debug("Closing server...");

    try {
      serverSocket.close();
    } catch (IOException ex) {
    }
    log.debug("Server socket was closed");

    if (!executor.isShutdown()) {
      executor.shutdown();
    }

    context.getNodes().clear();

    log.info("Server was closed");
  }
}
