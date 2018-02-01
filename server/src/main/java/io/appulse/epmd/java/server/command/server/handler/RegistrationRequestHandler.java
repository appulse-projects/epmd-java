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

import static io.appulse.epmd.java.core.model.Tag.ALIVE2_REQUEST;

import java.util.concurrent.atomic.AtomicInteger;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.request.Registration;
import io.appulse.epmd.java.core.model.response.RegistrationResult;
import io.appulse.epmd.java.server.command.server.Node;
import io.appulse.epmd.java.server.command.server.Request;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
class RegistrationRequestHandler implements RequestHandler {

  AtomicInteger count = new AtomicInteger(0);

  @Override
  public void handle (@NonNull Request request) {
    Registration registration = request.parse(Registration.class);
    log.info("Registering {} node...", registration.getName());

    val node = register(request, registration);
    val response = RegistrationResult.builder()
        .ok(node != null)
        .creation(node != null
                  ? node.getCreation()
                  : 0
        )
        .build();

    request.respond(response);
    if (node == null) {
      request.closeConnection();
    }
  }

  @Override
  public Tag getTag () {
    return ALIVE2_REQUEST;
  }

  private Node register (Request request, Registration registration) {
    return request.getContext().getNodes()
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
              .socket(request.getSocket())
              .build();
        });
  }
}
