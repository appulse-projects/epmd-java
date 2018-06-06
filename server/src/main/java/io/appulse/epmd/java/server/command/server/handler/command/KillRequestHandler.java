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

package io.appulse.epmd.java.server.command.server.handler.command;

import static io.appulse.epmd.java.core.model.Tag.KILL_REQUEST;
import static io.appulse.epmd.java.core.model.response.KillResult.OK;
import static io.netty.channel.ChannelFutureListener.CLOSE;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.request.Request;
import io.appulse.epmd.java.server.command.server.ServerState;

import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Killing EPMD server request handler.
 *
 * @since 0.4.0
 * @author Artem Labazin
 */
@Slf4j
class KillRequestHandler implements RequestHandler {

  @Override
  public void handle (@NonNull Request request, @NonNull ChannelHandlerContext context, @NonNull ServerState state) {
    log.debug("Processing {}", request);

    if (!state.getServerOptions().isChecks()) {
      log.warn("Option '-relaxed_command_check' is false, but someone trying to kill this EPMD");
      context.close();
      return;
    }

    state.getNodes().clear();
    log.debug("Nodes registry was cleared");

    context.writeAndFlush(OK)
        .addListener(CLOSE)
        .addListener(future -> {
          log.debug("Shutting down this EPMD");
          Runtime.getRuntime().exit(1);
        });
  }

  @Override
  public Tag getTag () {
    return KILL_REQUEST;
  }
}
