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

import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import io.appulse.epmd.java.core.mapper.serializer.MessageSerializer;
import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.utils.Bytes;

import lombok.SneakyThrows;

public class RequestTestUtil {

  @SneakyThrows
  public static Request createRequest (ByteArrayOutputStream output) {
    return createRequest(Bytes.allocate(), new ServerCommandOptions(), output);
  }

  @SneakyThrows
  public static Request createRequest (ServerCommandOptions options, ByteArrayOutputStream output) {
    return createRequest(Bytes.allocate(), options, output);
  }

  @SneakyThrows
  public static Request createRequest (Object request, ByteArrayOutputStream output) {
    byte[] bytes = serialize(request);
    return createRequest(Bytes.wrap(bytes), new ServerCommandOptions(), output);
  }

  @SneakyThrows
  public static Request createRequest (Object request, ServerCommandOptions options, ByteArrayOutputStream output) {
    byte[] bytes = serialize(request);

    Context context = Context.builder()
        .nodes(new ConcurrentHashMap<>())
        .commonOptions(new CommonOptions())
        .serverOptions(options)
        .build();

    Socket socket = mock(Socket.class);
    when(socket.getOutputStream()).thenReturn(output);

    return Request.builder()
        .context(context)
        .socket(socket)
        .payload(Bytes.wrap(bytes))
        .build();
  }

  @SneakyThrows
  public static Request createRequest (Bytes payload, ServerCommandOptions options, ByteArrayOutputStream output) {
    Context context = Context.builder()
        .nodes(new ConcurrentHashMap<>())
        .commonOptions(new CommonOptions())
        .serverOptions(options)
        .build();

    Socket socket = mock(Socket.class);
    when(socket.getOutputStream()).thenReturn(output);

    return Request.builder()
        .context(context)
        .socket(socket)
        .payload(payload)
        .build();
  }

  public static byte[] serialize (Object object) {
    return new MessageSerializer().serialize(object);
  }
}
