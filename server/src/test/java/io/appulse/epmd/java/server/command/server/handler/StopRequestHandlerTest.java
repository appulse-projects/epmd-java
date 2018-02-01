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
import static org.assertj.core.api.Assertions.assertThat;
import static io.appulse.epmd.java.core.model.response.StopResult.STOPPED;
import static io.appulse.epmd.java.core.model.response.StopResult.NOEXIST;
import static io.appulse.epmd.java.core.model.NodeType.R3_ERLANG;
import static io.appulse.epmd.java.core.model.Protocol.SCTP;
import static io.appulse.epmd.java.core.model.Version.R3;
import static io.appulse.epmd.java.core.model.Version.R4;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.stream.IntStream;

import io.appulse.epmd.java.core.model.request.Stop;
import io.appulse.epmd.java.server.command.server.Node;
import io.appulse.epmd.java.server.command.server.RequestTestUtil;
import io.appulse.epmd.java.server.command.server.ServerCommandOptions;

import org.junit.Test;
import lombok.val;

public class StopRequestHandlerTest {

  RequestHandler handler = new StopRequestHandler();

  @Test
  public void handleOk () {
    Stop requestObject = new Stop("node-1");

    val output = new ByteArrayOutputStream();
    val options = new ServerCommandOptions();
    options.setChecks(true);
    val request = RequestTestUtil.createRequest(requestObject, options, output);

    IntStream.range(0, 3)
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
        .forEach(it -> request.getContext().getNodes().put(it.getName(), it));

    handler.handle(request);
    assertThat(output.toByteArray())
        .isEqualTo(RequestTestUtil.serialize(STOPPED));
  }

  @Test
  public void handleNok () {
    Stop requestObject = new Stop("popa");

    val output = new ByteArrayOutputStream();
    val options = new ServerCommandOptions();
    options.setChecks(true);
    val request = RequestTestUtil.createRequest(requestObject, options, output);

    handler.handle(request);
    assertThat(output.toByteArray())
        .isEqualTo(RequestTestUtil.serialize(NOEXIST));
  }

  @Test
  public void handleNotAllowed () {
    val output = new ByteArrayOutputStream();
    val request = RequestTestUtil.createRequest(output);

    handler.handle(request);

    assertThat(output.toByteArray())
        .isEmpty();
  }

  @Test
  public void getTag () {
    assertThat(handler.getTag())
        .isEqualTo(STOP_REQUEST);
  }
}
