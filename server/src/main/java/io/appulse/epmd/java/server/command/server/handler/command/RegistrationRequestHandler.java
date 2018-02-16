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

import static io.appulse.epmd.java.core.model.Tag.ALIVE2_REQUEST;
import static io.netty.channel.ChannelFutureListener.CLOSE;

import java.util.concurrent.atomic.AtomicInteger;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.request.Registration;
import io.appulse.epmd.java.core.model.request.Request;
import io.appulse.epmd.java.core.model.response.RegistrationResult;
import io.appulse.epmd.java.server.command.server.Node;
import io.appulse.epmd.java.server.command.server.ServerState;

import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.4.0
 */
@Slf4j
class RegistrationRequestHandler implements RequestHandler {

  AtomicInteger count = new AtomicInteger(0);

  @Override
  public void handle (@NonNull Request request, @NonNull ChannelHandlerContext context, @NonNull ServerState state) {
    if (!(request instanceof Registration)) {
      val message = String.format("Invalid request object:%n%s", request);
      log.error(message);
      throw new IllegalArgumentException(message);
    }

    Registration registration = (Registration) request;
    log.info("Registering {} node...", registration.getName());

    val node = register(registration, state);
    log.debug("Registration result: {}", node);

    val response = RegistrationResult.builder()
        .ok(node != null)
        .creation(node != null
                  ? node.getCreation()
                  : 0
        )
        .build();
    log.debug("Response: {}", response);

    val future = context.writeAndFlush(response);
    if (!response.isOk()) {
      future.addListener(CLOSE);
    } else {
      context.channel().closeFuture().addListener(f -> {
        val name = node.getName();
        state.getNodes().remove(name);
        log.debug("Node {} was disconnected", name);
      });
    }
  }

  @Override
  public Tag getTag () {
    return ALIVE2_REQUEST;
  }

  private Node register (Registration registration, ServerState serverState) {
    return serverState.getNodes()
        .compute(registration.getName(), (key, value) -> {
          if (value != null) {
            return null;
          }
          return Node.builder()
              .name(registration.getName())
              .port(registration.getPort())
              .type(registration.getType())
              .protocol(registration.getProtocol())
              .high(registration.getHigh())
              .low(registration.getLow())
              .creation(count.incrementAndGet())
              .build();
        });
  }
}
