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

import java.net.InetAddress;
import java.util.List;

import io.appulse.epmd.java.core.model.request.GetEpmdInfo;
import io.appulse.epmd.java.core.model.response.EpmdInfo;
import io.appulse.epmd.java.core.model.response.EpmdInfo.NodeDescription;
import io.appulse.epmd.java.core.model.response.Response;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
final class CommandGetEpmdInfo extends CommandAbstract<GetEpmdInfo, List<NodeDescription>> {

  @Builder
  CommandGetEpmdInfo (InetAddress address, Integer port, GetEpmdInfo request) {
    super(address, port, request);
  }

  @Override
  public List<NodeDescription> get () {
    log.debug("requesting registered nodes in EPMD server");

    val requestBytes = getRequestBytes();
    try (val connection = createConnection()) {
      connection.send(requestBytes);
      val responseBytes = connection.receive();
      val response = Response.parse(responseBytes, EpmdInfo.class);
      return response.getNodes();
    }
  }
}
