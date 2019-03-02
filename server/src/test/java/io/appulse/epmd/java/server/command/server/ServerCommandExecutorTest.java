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

package io.appulse.epmd.java.server.command.server;

import static io.appulse.epmd.java.core.model.NodeType.R3_ERLANG;
import static io.appulse.epmd.java.core.model.Protocol.TCP;
import static io.appulse.epmd.java.core.model.Version.R6;
import static io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status.ACTIVE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import io.appulse.epmd.java.client.EpmdClient;
import io.appulse.epmd.java.client.exception.EpmdRegistrationException;
import io.appulse.epmd.java.core.model.request.Registration;
import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.utils.SocketUtils;
import io.appulse.utils.threads.AppulseExecutors;

import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerCommandExecutorTest {

  ExecutorService executorService = AppulseExecutors.newSingleThreadExecutor().build();

  EpmdClient client;

  ServerCommandExecutor server;

  Future<?> future;

  @BeforeEach
  void before () throws Exception {
    val port = SocketUtils.findFreePort()
        .orElseThrow(RuntimeException::new);

    val commonOptions = new CommonOptions();
    commonOptions.setPort(port);
    server = new ServerCommandExecutor(commonOptions, new ServerCommandOptions());

    future = executorService.submit(server::execute);

    SECONDS.sleep(1);

    client = new EpmdClient(port);
  }

  @AfterEach
  void after () {
    client.close();
    server.close();
    future.cancel(true);
  }

  @Test
  void empty () {
    // empty
  }

  @Test
  void register () throws Exception {
    val registration = Registration.builder()
        .name("registers")
        .port(8976)
        .type(R3_ERLANG)
        .protocol(TCP)
        .high(R6)
        .low(R6)
        .build();

    client.register(registration).get(3, SECONDS);

    val optional = client.lookup("registers", client.getPort()).get(3, SECONDS);
    assertThat(optional).isPresent();

    val nodeInfo = optional.get();
    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(nodeInfo.getPort())
          .isPresent()
          .hasValue(8976);

      softly.assertThat(nodeInfo.getType())
          .isPresent()
          .hasValue(R3_ERLANG);

      softly.assertThat(nodeInfo.getProtocol())
          .isPresent()
          .hasValue(TCP);

      softly.assertThat(nodeInfo.getHigh())
          .isPresent()
          .hasValue(R6);

      softly.assertThat(nodeInfo.getLow())
          .isPresent()
          .hasValue(R6);
    });

    assertThatThrownBy(() -> client.register(registration).get(3, SECONDS))
        .hasCauseInstanceOf(EpmdRegistrationException.class);

    client.stop("register");
  }

  @Test
  void dumpEmpty () throws Exception {
    val nodes = client.dump().get(3, SECONDS);
    assertThat(nodes)
        .isNotNull()
        .isEmpty();
  }

  @Test
  void dumpAll () throws Exception {
    val registration = Registration.builder()
        .name("dump")
        .port(19027)
        .type(R3_ERLANG)
        .protocol(TCP)
        .high(R6)
        .low(R6)
        .build();

    client.register(registration).get(3, SECONDS);
    val nodes = client.dump().get(3, SECONDS);
    assertThat(nodes)
        .isNotNull()
        .hasSize(1)
        .element(0).isNotNull();

    val nodeDump = nodes.get(0);
    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(nodeDump.getName())
          .isEqualTo("dump");

      softly.assertThat(nodeDump.getPort())
          .isEqualTo(19027);

      softly.assertThat(nodeDump.getFileDescriptor())
          .isNotEqualTo(0);

      softly.assertThat(nodeDump.getStatus())
          .isEqualTo(ACTIVE);
    });
  }
}
