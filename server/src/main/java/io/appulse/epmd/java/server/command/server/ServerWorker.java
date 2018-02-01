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
import io.appulse.utils.Bytes;
import io.appulse.utils.SocketUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class ServerWorker implements Runnable {

  @NonNull
  Socket socket;

  @NonNull
  Context context;

  @Override
  public void run () {
    log.debug("Start server worker");

    val length = SocketUtils.readBytes(socket, Short.BYTES).getShort();
    log.debug("Incoming message length is: {}", length);

    val body = SocketUtils.read(socket, length);
    log.debug("Readed message body ({} bytes)", body.length);

    val bytes = Bytes.allocate(Short.BYTES + length)
        .put2B(length)
        .put(body);

    val tag = Tag.of(bytes.getByte(2));
    if (tag == UNDEFINED) {
      log.error("Undefined incoming message tag");
      throw new IllegalArgumentException();
    }
    log.debug("Incoming message tag: {}", tag);

    val handler = RequestHandler.ALL.get(tag);
    if (handler == null) {
      log.error("There is no handler for tag {}", tag);
      throw new IllegalArgumentException();
    }
    log.debug("Request's handler: {}", handler);

    val request = Request.builder()
        .context(context)
        .socket(socket)
        .payload(bytes.position(0))
        .build();

    log.debug("Incoming request: {}", request);

    handler.handle(request);

    log.debug("End server worker");
  }
}
