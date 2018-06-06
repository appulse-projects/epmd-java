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

package io.appulse.epmd.java.server.command.server.handler;

import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.core.model.request.Request;
import io.appulse.epmd.java.server.command.server.ServerState;
import io.appulse.epmd.java.server.command.server.handler.command.RequestHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Server's main request handler.
 *
 * @since 0.4.0
 * @author Artem Labazin
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ServerHandler extends ChannelInboundHandlerAdapter {

  @NonNull
  ServerState serverState;

  @Override
  public void handlerAdded (ChannelHandlerContext ctx) throws Exception {
    log.debug("Server handler was added");
  }

  @Override
  public void channelRead (ChannelHandlerContext context, Object obj) throws Exception {
    val request = (Request) obj;

    val handler = RequestHandler.ALL.get(request.getTag());
    if (handler == null) {
      val message = String.format("There is no handler for tag: '%s'", request.getTag());
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    log.debug("Request's handler: {}", handler);

    handler.handle(request, context, serverState);
  }

  @Override
  public void exceptionCaught (ChannelHandlerContext context, Throwable cause) throws Exception {
    val message = String.format("Error during channel connection with %s",
                                context.channel().remoteAddress().toString());

    log.error(message, cause);
    context.close();
  }
}
