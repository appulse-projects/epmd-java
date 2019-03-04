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

package io.appulse.epmd.java.server.command.server.handler;

import java.util.List;

import io.appulse.epmd.java.core.model.request.Request;
import io.appulse.utils.Bytes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Netty's decoder for requests.
 *
 * @since 0.4.0
 * @author Artem Labazin
 */
@Slf4j
public class RequestDecoder extends ReplayingDecoder<Request> {

  @Override
  public void exceptionCaught (ChannelHandlerContext context, Throwable cause) throws Exception {
    val message = String.format("Error during channel connection with %s",
                                context.channel().remoteAddress().toString());

    log.error(message, cause);
    context.close();
  }

  @Override
  protected void decode (ChannelHandlerContext context, ByteBuf buffer, List<Object> out) throws Exception {
    val length = buffer.readShort();
    log.debug("Received message length is: {}", length);
    if (length == 0) {
      return;
    }

    ByteBuf buf = buffer.readBytes(length);
    val body = new byte[length];
    buf.getBytes(0, body);
    log.debug("Readed message body:\n{}", body);

    Bytes bytes = Bytes.allocate(Short.BYTES + length)
        .write2B(length)
        .writeNB(body);

    val request = Request.parse(bytes);
    log.debug("Received request: {}", request);
    out.add(request);
  }
}
