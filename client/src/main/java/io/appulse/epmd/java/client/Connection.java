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

package io.appulse.epmd.java.client;

import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import io.appulse.epmd.java.client.exception.EpmdConnectionException;
import io.appulse.epmd.java.core.mapper.deserializer.MessageDeserializer;
import io.appulse.epmd.java.core.mapper.serializer.MessageSerializer;
import io.appulse.epmd.java.core.model.response.RegistrationResult;
import io.appulse.utils.StreamReader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.2.2
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class Connection implements Closeable {

  private static final int CONNECT_TIMEOUT;

  private static final MessageSerializer SERIALIZER;

  private static final MessageDeserializer DESERIALIZER;

  static {
    CONNECT_TIMEOUT = (int) SECONDS.toMillis(5);
    SERIALIZER = new MessageSerializer();
    DESERIALIZER = new MessageDeserializer();
  }

  @NonNull
  InetAddress address;

  int port;

  Socket socket = new Socket();

  public void send (@NonNull Object request) throws EpmdConnectionException {
    log.debug("Sending: {}", request);

    val bytes = SERIALIZER.serialize(request);

    connect();

    try {
      socket.getOutputStream().write(bytes);
      socket.getOutputStream().flush();
    } catch (IOException ex) {
      throw new EpmdConnectionException(ex);
    }

    log.debug("Message {} was sent", request);
  }

  public <T> T send (@NonNull Object request, @NonNull Class<T> responseType) throws EpmdConnectionException {
    send(request);

    byte[] messageBytes;
    try {
      messageBytes = responseType == RegistrationResult.class
                     ? StreamReader.read(socket, 4)
                     : StreamReader.read(socket);
    } catch (Exception ex) {
      throw new EpmdConnectionException(ex);
    }

    log.debug("Received bytes:\n{}", messageBytes);
    val result = DESERIALIZER.deserialize(messageBytes, responseType);

    log.debug("Received message:\n{}", result);
    return result;
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

  public boolean isClosed () {
    return socket.isClosed();
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
    } catch (IOException ex) {
      val message = String.format("Couldn't connect to EPMD server (%s:%d), maybe it is down",
                                  address.toString(), port);
      log.error(message);
      throw new EpmdConnectionException(message, ex);
    }

    log.debug("EPMD connection to {}:{} was established", address, port);
  }
}
