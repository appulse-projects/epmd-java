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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.appulse.epmd.java.client.EpmdClient;
import io.appulse.epmd.java.client.exception.EpmdRegistrationException;
import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.utils.SocketUtils;
import io.appulse.utils.test.TestMethodNamePrinter;
import io.appulse.utils.threads.AppulseExecutors;

import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 *
 * @author Artem Labazin
 * @since 0.4.0
 */
public class ServerCommandExecutorTest {

  @Rule
  public TestRule watcher = new TestMethodNamePrinter();

  ExecutorService executorService = AppulseExecutors.newSingleThreadExecutor().build();

  EpmdClient client;

  ServerCommandExecutor server;

  Future<?> future;

  @Before
  public void before () throws Exception {
    val port = SocketUtils.findFreePort()
        .orElseThrow(RuntimeException::new);

    val commonOptions = new CommonOptions();
    commonOptions.setPort(port);
    server = new ServerCommandExecutor(commonOptions, new ServerCommandOptions());

    future = executorService.submit(server::execute);

    TimeUnit.SECONDS.sleep(1);

    client = new EpmdClient(port);
  }

  @After
  public void after () {
    client.close();
    server.close();
    future.cancel(true);
  }

  @Test
  public void empty () {
    // empty
  }

  @Test
  public void register () {
    client.register("register", 8971, R3_ERLANG, TCP, R6, R6);

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

    assertThatThrownBy(() -> client.register("register", 8971, R3_ERLANG, TCP, R6, R6))
        .isInstanceOf(EpmdRegistrationException.class);

    client.stop("register");
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
