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

import static io.appulse.epmd.java.core.model.Tag.DUMP_REQUEST;
import static io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status.ACTIVE;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.response.EpmdDump;
import io.appulse.epmd.java.core.model.response.EpmdDump.EpmdDumpBuilder;
import io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump;
import io.appulse.epmd.java.server.command.server.Request;

import lombok.NonNull;

class GetEpmdDumpRequestHandler implements RequestHandler {

  @Override
  public void handle (@NonNull Request request) {
    EpmdDumpBuilder builder = EpmdDump.builder()
        .port(request.getContext().getPort());

    request.getContext()
        .getNodes()
        .values()
        .stream()
        .map(it -> new NodeDump(ACTIVE, it.getName(), it.getPort(), -1))
        .forEach(builder::node);

    request.respondAndClose(builder.build());
  }

  @Override
  public Tag getTag () {
    return DUMP_REQUEST;
  }
}