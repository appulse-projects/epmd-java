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

package io.appulse.epmd.java.server.command.server;

import static org.assertj.core.api.Assertions.assertThat;
import static io.appulse.epmd.java.core.model.NodeType.R3_ERLANG;
import static io.appulse.epmd.java.core.model.Protocol.TCP;
import static io.appulse.epmd.java.core.model.Version.R6;
import static io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status.ACTIVE;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.appulse.epmd.java.client.EpmdClient;
import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.utils.SocketUtils;

import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerCommandExecutorTest {

  EpmdClient client;

  ServerCommandExecutor server;

  ExecutorService executorService;

  @Before
  public void before () {
    val port = SocketUtils.findFreePort()
        .orElseThrow(RuntimeException::new);

    val commonOptions = new CommonOptions();
    commonOptions.setPort(port);
    server = new ServerCommandExecutor(commonOptions, new ServerCommandOptions());

    executorService = Executors.newSingleThreadExecutor();
    executorService.execute(() -> server.execute());

    client = new EpmdClient(port);
  }

  @After
  public void after () {
    client.close();
    server.close();
    executorService.shutdownNow();
    executorService = null;
  }

  @Test
  public void empty () {
    // empty
  }

  @Test
  public void register () {
    val creation = client.register("register", 8971, R3_ERLANG, TCP, R6, R6);
    assertThat(creation).isNotEqualTo(0);

    val optional = client.lookup("register");
    assertThat(optional).isPresent();

    val nodeInfo = optional.get();
    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(nodeInfo.getPort())
          .isPresent()
          .hasValue(8971);

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
  }

  @Test
  public void dumpEmpty () {
    val nodes = client.dumpAll();
    assertThat(nodes)
        .isNotNull()
        .isEmpty();
  }

  @Test
  public void dumpAll () {
    client.register("dump", 19027, R3_ERLANG, TCP, R6, R6);
    val nodes = client.dumpAll();
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
