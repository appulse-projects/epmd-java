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

import static lombok.AccessLevel.PRIVATE;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
class Server extends Thread implements Closeable {

  ExecutorService executor;

  ServerSocket serverSocket;

  Set<InetAddress> addresses;

  boolean debug;

  @SneakyThrows
  Server (@NonNull Set<InetAddress> addresses, int port, boolean debug) {
    executor = Executors.newCachedThreadPool();
    serverSocket = new ServerSocket(port);
    this.addresses = addresses;
    this.debug = debug;
  }

  @Override
  @SneakyThrows
  public void run () {
    while (!isInterrupted()) {
      val socket = serverSocket.accept();
      if (!addresses.isEmpty() && !addresses.contains(socket.getInetAddress())) {
        socket.close();
        continue;
      }
      if (debug) {
        System.out.println();
      }
      val listener = new ServerHandler(socket, null);
      executor.execute(listener);
    }
  }

  @Override
  @SneakyThrows
  public void close () {
    interrupt();
    serverSocket.close();
    if (!executor.isShutdown()) {
      executor.shutdown();
    }
  }
}
