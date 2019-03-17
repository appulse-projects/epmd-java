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

package io.appulse.epmd.java.client;

import static lombok.AccessLevel.PRIVATE;

import java.net.InetAddress;
import java.util.Map;

import io.appulse.epmd.java.client.exception.EpmdRegistrationException;
import io.appulse.epmd.java.core.model.request.Registration;
import io.appulse.epmd.java.core.model.response.RegistrationResult;
import io.appulse.epmd.java.core.model.response.Response;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.experimental.FieldDefaults;

/**
 * A command for registration a node in a remote EPMD server.
 *
 * @since 2.0.0
 * @author Artem Labazin
 */
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class CommandRegistration extends CommandAbstract<Registration, RegistrationResult> {

  Map<String, Connection> registered;

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
  CommandRegistration (InetAddress address, Integer port, Registration request, Map<String, Connection> registered) {
    super(address, port, request);
    this.registered = registered;
  }

  @Override
  public RegistrationResult get () {
    val request = getRequest();
    log.debug("Registering: '{}'", request.getName());

    val connection = createConnection();

    val requestBytes = request.toBytes();
    connection.send(requestBytes);

    val responseBytes = connection.receive(4);
    val result = Response.parse(responseBytes, RegistrationResult.class);

    if (!result.isOk()) {
      connection.close();
      log.error("'{}' wasn't registered successfully", request.getName());
      throw new EpmdRegistrationException();
    }

    registered.put(request.getName(), connection);
    return result;
  }
}
