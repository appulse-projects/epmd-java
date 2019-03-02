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

import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import io.appulse.epmd.java.client.exception.EpmdConnectionException;
import io.appulse.utils.SocketUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * EPMD connection.
 *
 * @since 0.2.2
 * @author Artem Labazin
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class Connection implements Closeable {

  private static final int CONNECT_TIMEOUT;

  private static final int READ_TIMEOUT;

  static {
    CONNECT_TIMEOUT = (int) SECONDS.toMillis(5);
    READ_TIMEOUT = CONNECT_TIMEOUT * 2;
  }

  @NonNull
  InetAddress address;

  int port;

  Socket socket = new Socket();

  void send (@NonNull byte[] bytes) {
    log.debug("Sending: {}", bytes);

    connect();

    try {
      socket.getOutputStream().write(bytes);
      socket.getOutputStream().flush();
    } catch (IOException ex) {
      throw new EpmdConnectionException(ex);
    }

    log.debug("Message {} was sent", bytes);
  }

  byte[] receive () {
    byte[] bytes;
    try {
      bytes = SocketUtils.read(socket);
    } catch (Exception ex) {
      throw new EpmdConnectionException(ex);
    }
    log.debug("Received bytes:\n{}", bytes);
    return bytes;
  }

  byte[] receive (int length) {
    byte[] bytes;
    try {
      bytes = SocketUtils.read(socket, length);
    } catch (Exception ex) {
      throw new EpmdConnectionException(ex);
    }
    log.debug("Received bytes:\n{}", bytes);
    return bytes;
  }

  @Override
  @SneakyThrows
  public void close () {
    if (socket.isClosed()) {
      log.debug("EPMD connection was already closed");
      return;
    }

    socket.close();

    log.debug("EPMD connection (from localhost:{}, to {}) was closed",
              socket.getLocalPort(), socket.getRemoteSocketAddress());
  }

  boolean isClosed () {
    return socket.isClosed();
  }

  boolean isConnected () {
    return socket.isConnected();
  }

  private void connect () {
    if (socket.isConnected()) {
      log.debug("EPMD connection was already connected");
      return;
    }

    val socketAddress = new InetSocketAddress(address, port);
    try {
      socket.setTcpNoDelay(true);
      socket.connect(socketAddress, CONNECT_TIMEOUT);
      socket.setSoTimeout(READ_TIMEOUT);
    } catch (IOException ex) {
      val message = String.format("Couldn't connect to EPMD server (%s:%d), maybe it is down",
                                  address.toString(), port);
      log.error(message);
      throw new EpmdConnectionException(message, ex);
    }

    log.debug("EPMD connection to {}:{} was established", address, port);
  }
}
