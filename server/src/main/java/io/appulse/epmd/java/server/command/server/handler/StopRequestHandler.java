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

import static io.appulse.epmd.java.core.model.Tag.STOP_REQUEST;
import static io.appulse.epmd.java.core.model.response.StopResult.NOEXIST;
import static io.appulse.epmd.java.core.model.response.StopResult.STOPPED;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.request.Stop;
import io.appulse.epmd.java.server.command.server.Request;

import lombok.NonNull;
import lombok.val;

class StopRequestHandler implements RequestHandler {

  @Override
  public void handle (@NonNull Request request) {
    if (!request.getContext().getServerOptions().isChecks()) {
      request.closeConnection();
      return;
    }

    val stop = request.parse(Stop.class);
    val result = request.getContext()
        .getNodes()
        .remove(stop.getName());

    val response = result == null
                   ? NOEXIST
                   : STOPPED;

    request.respondAndClose(response);
  }

  @Override
  public Tag getTag () {
    return STOP_REQUEST;
  }
}
