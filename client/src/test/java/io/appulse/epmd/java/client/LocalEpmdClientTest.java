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
import static io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status.ACTIVE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.appulse.epmd.java.client.exception.EpmdConnectionException;
import io.appulse.epmd.java.client.exception.EpmdRegistrationException;
import io.appulse.epmd.java.client.exception.EpmdRegistrationNameConflictException;
import io.appulse.epmd.java.core.model.request.Registration;

import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class LocalEpmdClientTest {

  EpmdClient client;

  @BeforeAll
  static void beforeAll () {
    if (!LocalEpmdHelper.exists()) {
      throw new IllegalStateException("Could not find local EPMD. Skipping test");
    }
  }

  @BeforeEach
  void beforeEach () {
    LocalEpmdHelper.run();
    client = new EpmdClient();
  }

  @AfterEach
  void afterEach () {
    if (client != null) {
      client.close();
      client = null;
    }
    LocalEpmdHelper.kill();
  }

  @Test
  void twoRegisters () throws Exception {
    val registration = Registration.builder()
        .name("register")
        .port(8971)
        .type(R3_ERLANG)
        .protocol(TCP)
        .high(R6)
        .low(R6)
        .build();

    val result = client.register(registration).get(3, SECONDS);
    assertThat(result)
        .isNotNull();

    assertThat(result.getCreation())
        .isNotEqualTo(0);

    assertThatThrownBy(() -> client.register(registration).get(3, SECONDS))
        .hasCauseExactlyInstanceOf(EpmdRegistrationNameConflictException.class);
  }

  @Test
  void register () throws Exception {
    val registration = Registration.builder()
        .name("register")
        .port(61_123)
        .type(R3_ERLANG)
        .protocol(TCP)
        .high(R6)
        .low(R6)
        .build();

    val creation = client.register(registration)
        .get(3, SECONDS)
        .getCreation();

    assertThat(creation)
        .isNotEqualTo(0);

    val optional = client.lookup("register").get(3, SECONDS);
    assertThat(optional).isPresent();

    val nodeInfo = optional.get();
    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(nodeInfo.getPort())
          .isPresent()
          .hasValue(61_123);

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
  void invalidRegistration () {
    val registration = Registration.builder()
        .name("popa")
        .port(8971)
        .type(R3_ERLANG)
        .protocol(TCP)
        .high(R6)
        .low(R6)
        .build();

    try (EpmdClient client2 = new EpmdClient(8099)) {
      assertThatThrownBy(() -> client2.register(registration).get(3, SECONDS))
          .hasCauseInstanceOf(EpmdRegistrationException.class);
    }
  }

  @Test
  void lookupAndRegistration () throws Exception {
    val registration1 = Registration.builder()
        .name("node-1")
        .port(61_111)
        .type(R3_ERLANG)
        .protocol(TCP)
        .high(R6)
        .low(R6)
        .build();

    assertThat(client.register(registration1).get(3, SECONDS).getCreation())
        .isNotEqualTo(0);

    val optional1 = client.lookup("node-1").get(3, SECONDS);
    assertThat(optional1)
        .isPresent();
    assertThat(optional1.get().isOk())
        .isTrue();

    assertThat(client.lookup("node-2").get(3, SECONDS))
        .isNotPresent();

    val registration2 = Registration.builder()
        .name("node-2")
        .port(61_112)
        .type(R3_ERLANG)
        .protocol(TCP)
        .high(R6)
        .low(R6)
        .build();

    assertThat(client.register(registration2).get(3, SECONDS))
        .isNotEqualTo(0);

    val optional2 = client.lookup("node-2").get(3, SECONDS);
    assertThat(optional2)
        .isPresent();
    assertThat(optional2.get().isOk())
        .isTrue();
  }

  @Test
  void connectionBroken () {
    assertThatThrownBy(() -> client.lookup("popa", 8099).get(3, SECONDS))
        .hasCauseInstanceOf(EpmdConnectionException.class);
  }

  @Test
  @Disabled
  void dumpEmpty () throws Exception {
    val nodes = client.dump().get(3, SECONDS);
    assertThat(nodes)
        .isNotNull()
        .isEmpty();
  }

  @Test
  void dump () throws Exception {
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
        .isNotEmpty();

    val nodeDump = nodes.stream()
        .filter(it -> it.getName().equals("dump"))
        .findFirst()
        .get();

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

  @Test
  void kill () throws Exception {
    assertThat(client.kill().get(3, SECONDS))
        .as("EPMD client wasn't killed")
        .isTrue();
  }
}
