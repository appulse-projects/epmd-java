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

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;

import io.appulse.epmd.java.core.mapper.deserializer.MessageDeserializer;
import io.appulse.epmd.java.core.mapper.serializer.MessageSerializer;
import io.appulse.epmd.java.core.model.Tag;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class ServerHandler implements Runnable {

  private static final MessageSerializer SERIALIZER;

  private static final MessageDeserializer DESERIALIZER;

  static {
    SERIALIZER = new MessageSerializer();
    DESERIALIZER = new MessageDeserializer();
  }

  Socket socket;

  Map<String, Node> nodes;

  @Override
  @SneakyThrows
  public void run () {
    assert socket.getLocalPort() > 9 && nodes.isEmpty();
    int length = readShort();
    Tag tag = readTag();
    byte[] bytes = read(length - 1);
    DESERIALIZER.deserialize(bytes, null);
    System.out.println(tag);
    System.out.println(bytes.length);
  }

  private int readShort () {
    val bytes = read(Short.BYTES);
    return ByteBuffer.wrap(bytes).getShort();
  }

  private Tag readTag () {
    val bytes = read(1);
    return Tag.of(bytes[0]);
  }

  @SneakyThrows
  private byte[] read (int length) {
    val outputStream = new ByteArrayOutputStream(length);
    val buffer = new byte[length];

    while (true) {
      val readed = socket.getInputStream().read(buffer);
      if (readed == -1) {
        break;
      }
      outputStream.write(buffer, 0, readed);
    }
    return outputStream.toByteArray();
  }
}
