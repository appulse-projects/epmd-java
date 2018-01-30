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

import java.net.Socket;

import io.appulse.epmd.java.core.mapper.deserializer.MessageDeserializer;
import io.appulse.epmd.java.core.mapper.serializer.MessageSerializer;
import io.appulse.utils.Bytes;

import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

@Value
@Builder
public final class Request {

  private static final MessageSerializer SERIALIZER;

  private static final MessageDeserializer DESERIALIZER;

  static {
    SERIALIZER = new MessageSerializer();
    DESERIALIZER = new MessageDeserializer();
  }

  @NonNull
  Context context;

  @NonNull
  Socket socket;

  @NonNull
  Bytes payload;

  public <T> T parse (@NonNull Class<T> type) {
    return DESERIALIZER.deserialize(payload.array(), type);
  }

  @SneakyThrows
  public void respond (@NonNull Object response) {
    byte[] bytes = SERIALIZER.serialize(response);
    socket.getOutputStream().write(bytes);
  }

  public void respondAndClose (@NonNull Object response) {
    respond(response);
    closeConnection();
  }

  @SneakyThrows
  public void closeConnection () {
    socket.close();
  }
}
