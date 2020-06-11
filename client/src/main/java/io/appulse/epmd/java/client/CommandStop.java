/*
 * Copyright 2020 the original author or authors.
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

package io.appulse.epmd.java.client;

import static io.appulse.epmd.java.core.model.response.StopResult.STOPPED;

import java.net.InetAddress;

import io.appulse.epmd.java.core.model.request.Stop;
import io.appulse.epmd.java.core.model.response.Response;
import io.appulse.epmd.java.core.model.response.StopResult;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * A command for stopping a remote EPMD server.
 *
 * @since 2.0.0
 * @author Artem Labazin
 */
@Slf4j
final class CommandStop extends CommandAbstract<Stop, Boolean> {

  /**
   * Constructs the command object.
   *
   * @param address the remote EPMD server's inet address
   *
   * @param port the remote EPMD server's port
   *
   * @param request the command's request to the remote EPMD server
   */
  @Builder
  CommandStop (InetAddress address, Integer port, Stop request) {
    super(address, port, request);
  }

  @Override
  public Boolean get () {
    val request = getRequest();
    log.debug("stopping '{}'", request.getName());

    val requestBytes = request.toBytes();
    try (val connection = createConnection()) {
      connection.send(requestBytes);
      val responseBytes = connection.receive();
      val response = Response.parse(responseBytes, StopResult.class);
      return response == STOPPED;
    }
  }
}
