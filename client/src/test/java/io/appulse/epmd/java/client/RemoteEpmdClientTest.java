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

import static io.appulse.epmd.java.core.model.NodeType.R3_ERLANG;
import static io.appulse.epmd.java.core.model.Protocol.TCP;
import static io.appulse.epmd.java.core.model.Version.R6;
import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;

@Slf4j
@FieldDefaults(level = PRIVATE)
class RemoteEpmdClientTest {

  static final GenericContainer<?> ECHO = new GenericContainer<>("xxlabaza/echo-service-elixir:latest")
      .withCommand("--cookie=secret", "--name=echo@localhost")
      .withLogConsumer(frame -> log.info(((OutputFrame) frame).getUtf8String()))
      .withExposedPorts(4369)
      .withStartupTimeout(Duration.ofSeconds(10))
      .waitingFor(new GenericContainer.AbstractWaitStrategy() {

        @Override
        @SneakyThrows
        protected void waitUntilReady () {
          SECONDS.sleep(2);
        }
      });

  EpmdClient client;

  @BeforeAll
  static void beforeAll () {
    ECHO.start();
  }

  @AfterAll
  static void afterAll () {
    ECHO.stop();
  }

  @BeforeEach
  void beforeEach () {
    client = new EpmdClient(ECHO.getMappedPort(4369));
  }

  @AfterEach
  void afterEach () {
    if (client != null) {
      client.close();
      client = null;
    }
  }

  @Test
  void isContainerRunning () {
    assertThat(ECHO.isRunning())
        .as("Conteiner is not running")
        .isEqualTo(true);
  }

  @Test
  void lookup () {
    val optional = client.lookup("echo@localhost", ECHO.getMappedPort(4369)).join();
    assertThat(optional)
        .isPresent();

    val nodeInfo = optional.get();

    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(nodeInfo.isOk())
          .isEqualTo(true);

      softly.assertThat(nodeInfo.getPort())
          .isPresent();

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

      softly.assertThat(nodeInfo.getName())
          .isPresent()
          .hasValue("echo");
    });
  }

  @Test
  void getLocalNodes () {
    val nodes = client.getNodes(ECHO.getMappedPort(4369)).join();
    assertThat(nodes)
        .isNotNull()
        .hasSize(1)
        .element(0).isNotNull();

    val nodeDescription = nodes.get(0);
    assertThat(nodeDescription.getName())
        .isEqualTo("echo");
    assertThat(nodeDescription.getPort())
        .isGreaterThan(0);
  }

  @Test
  void getRemoteNodes () {
    val nodes = client.getNodes(ECHO.getContainerIpAddress(), ECHO.getMappedPort(4369)).join();
    assertThat(nodes)
        .isNotNull()
        .hasSize(1)
        .element(0).isNotNull();

    val nodeDescription = nodes.get(0);
    assertThat(nodeDescription.getName())
        .isEqualTo("echo");
    assertThat(nodeDescription.getPort())
        .isGreaterThan(0);
  }
}
