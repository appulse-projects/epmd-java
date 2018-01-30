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

import static io.appulse.epmd.java.core.model.Tag.UNDEFINED;
import static lombok.AccessLevel.PRIVATE;

import java.net.Socket;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.server.command.server.handler.RequestHandler;
import io.appulse.utils.StreamReader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class ServerWorker implements Runnable {

  @NonNull
  Socket socket;

  @NonNull
  Context context;

  @Override
  public void run () {
    val bytes = StreamReader.readBytes(socket);

    val length = bytes.getShort();
    if (length != bytes.remaining()) {
      throw new IllegalArgumentException();
    }

    val tag = Tag.of(bytes.getByte());
    if (tag == UNDEFINED) {
      throw new IllegalArgumentException();
    }

    val handler = RequestHandler.ALL.get(tag);
    if (handler == null) {
      throw new IllegalArgumentException();
    }

    val request = Request.builder()
        .context(context)
        .socket(socket)
        .payload(bytes.position(0))
        .build();

    handler.handle(request);
  }
}
