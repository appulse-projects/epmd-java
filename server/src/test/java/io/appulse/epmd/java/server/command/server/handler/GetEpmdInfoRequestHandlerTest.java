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

import static io.appulse.epmd.java.core.model.Tag.NAMES_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Collections.emptyList;
import static io.appulse.epmd.java.core.model.NodeType.R3_ERLANG;
import static io.appulse.epmd.java.core.model.Protocol.SCTP;
import static io.appulse.epmd.java.core.model.Version.R3;
import static io.appulse.epmd.java.core.model.Version.R4;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.stream.IntStream;

import io.appulse.epmd.java.core.model.response.EpmdInfo;
import io.appulse.epmd.java.core.model.response.EpmdInfo.NodeDescription;
import io.appulse.epmd.java.server.command.server.Node;
import io.appulse.epmd.java.server.command.server.RequestTestUtil;

import org.junit.Test;
import lombok.val;

public class GetEpmdInfoRequestHandlerTest {

  RequestHandler handler = new GetEpmdInfoRequestHandler();

  @Test
  public void handleEmpty () {
    val output = new ByteArrayOutputStream();
    val request = RequestTestUtil.createRequest(output);

    val info = EpmdInfo.builder()
        .port(request.getContext().getPort())
        .nodes(emptyList())
        .build();

    handler.handle(request);
    assertThat(output.toByteArray())
        .isEqualTo(RequestTestUtil.serialize(info));
  }

  @Test
  public void handleWithNodes () {
    val output = new ByteArrayOutputStream();
    val request = RequestTestUtil.createRequest(output);

    val nodes = IntStream.range(0, 3)
        .boxed()
        .map(it -> Node.builder()
            .name("node-" + it)
            .port(9090)
            .type(R3_ERLANG)
            .protocol(SCTP)
            .high(R4)
            .low(R3)
            .creation(it)
            .socket(new Socket())
            .build()
        )
        .peek(it -> request.getContext().getNodes().put(it.getName(), it))
        .collect(toList());

    val info = EpmdInfo.builder()
        .port(request.getContext().getPort())
        .nodes(nodes.stream()
            .map(it -> NodeDescription.builder()
                .name(it.getName())
                .port(it.getPort())
                .build()
            )
            .collect(toList())
        )
        .build();

    handler.handle(request);
    assertThat(output.toByteArray())
        .isEqualTo(RequestTestUtil.serialize(info));
  }

  @Test
  public void getTag () {
    assertThat(handler.getTag())
        .isEqualTo(NAMES_REQUEST);
  }
}