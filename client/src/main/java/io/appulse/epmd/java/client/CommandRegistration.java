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

import java.util.function.Supplier;

import io.appulse.epmd.java.client.exception.EpmdRegistrationException;
import io.appulse.epmd.java.core.model.request.Registration;
import io.appulse.epmd.java.core.model.response.RegistrationResult;
import io.appulse.epmd.java.core.model.response.Response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@Value
@Builder
class CommandRegistration implements Supplier<RegistrationResult> {

  @NonNull
  ConnectionManager connectionManager;

  @NonNull
  Registration request;

  @Override
  public RegistrationResult get () {
    log.debug("Registering: '{}'", request.getName());

    val connection = connectionManager.connect();

    val requestBytes = request.toBytes();
    connection.send(requestBytes);

    val responseBytes = connection.receive(4);
    val result = Response.parse(responseBytes, RegistrationResult.class);

    if (!result.isOk()) {
      connection.close();
      log.error("'{}' wasn't registered successfully", request.getName());
      throw new EpmdRegistrationException();
    }

    return result;
  }
}
