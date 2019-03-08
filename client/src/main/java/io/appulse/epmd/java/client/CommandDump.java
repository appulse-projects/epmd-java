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

import io.appulse.epmd.java.core.model.request.GetEpmdDump;
import io.appulse.epmd.java.core.model.response.EpmdDump;
import io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump;
import io.appulse.epmd.java.core.model.response.Response;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * A command for getting a dump information from a remote EPMD server.
 *
 * @since 2.0.0
 * @author Artem Labazin
 */
@Slf4j
final class CommandDump extends CommandAbstract<GetEpmdDump, List<NodeDump>> {

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
  CommandDump (InetAddress address, Integer port, GetEpmdDump request) {
    super(address, port, request);
  }

  @Override
  public List<NodeDump> get () {
    log.debug("requesting debug info");

    val requestBytes = getRequestBytes();
    try (val connection = createConnection();) {
      connection.send(requestBytes);
      val responseBytes = connection.receive();
      val response = Response.parse(responseBytes, EpmdDump.class);
      return response.getNodes();
    }
  }
}
