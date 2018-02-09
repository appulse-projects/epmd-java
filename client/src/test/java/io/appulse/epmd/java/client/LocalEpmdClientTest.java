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

package io.appulse.epmd.java.client;

import static io.appulse.epmd.java.core.model.NodeType.R3_ERLANG;
import static io.appulse.epmd.java.core.model.Protocol.TCP;
import static io.appulse.epmd.java.core.model.Version.R6;
import static io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status.ACTIVE;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status.OLD_OR_UNUSED;

import io.appulse.epmd.java.client.exception.EpmdConnectionException;
import io.appulse.epmd.java.client.exception.EpmdRegistrationException;
import io.appulse.epmd.java.client.util.CheckLocalEpmdExists;
import io.appulse.epmd.java.client.util.LocalEpmdHelper;
import io.appulse.epmd.java.client.util.TestNamePrinter;

import lombok.experimental.FieldDefaults;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 *
 * @author Artem Labazin
 * @since 0.2.2
 */
@FieldDefaults(level = PRIVATE)
public class LocalEpmdClientTest {

  @ClassRule
  public static CheckLocalEpmdExists localEpmdExists = new CheckLocalEpmdExists();

  @Rule
  public TestRule watcher = new TestNamePrinter();

  EpmdClient client;

  @Before
  public void before () {
    LocalEpmdHelper.run();
    client = new EpmdClient();
  }

  @After
  public void after () {
    if (client != null) {
      client.clearCaches();
      client.close();
      client = null;
    }
    LocalEpmdHelper.kill();
  }

  @Test
  public void twoRegisters () throws Exception {
    val creation = client.register("register", 8971, R3_ERLANG, TCP, R6, R6);
    assertThat(creation).isNotEqualTo(0);

    assertThatExceptionOfType(EpmdRegistrationException.class)
          .isThrownBy(() -> client.register("register", 8971, R3_ERLANG, TCP, R6, R6));
  }

  @Test
  public void register () throws Exception {
    val creation = client.register("register", 61_123, R3_ERLANG, TCP, R6, R6);
    assertThat(creation).isNotEqualTo(0);

    val optional = client.lookup("register");
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
  public void invalidRegistration () {
    try (EpmdClient client2 = new EpmdClient(8091)) {
      assertThatExceptionOfType(EpmdRegistrationException.class)
          .isThrownBy(() -> client2.register("popa", 8971, R3_ERLANG, TCP, R6, R6));
    }
  }

  @Test
  public void connectionBroken () {
    try (EpmdClient client2 = new EpmdClient(8091)) {
      assertThatExceptionOfType(EpmdConnectionException.class)
          .isThrownBy(() -> client2.lookup("popa"));
    }
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

//   @Test
  public void stop () {
    client.register("stopped", 19028, R3_ERLANG, TCP, R6, R6);

    client.stop("stopped");

    val nodes = client.dumpAll();
    assertThat(nodes)
        .isNotEmpty();

    val node = nodes.stream()
        .filter(it -> "stopped".equals(it.getName()))
        .findFirst()
        .orElse(null);

    assertThat(node)
        .isNotNull()
        .extracting("status").isEqualTo(OLD_OR_UNUSED);
  }

  @Test
  public void kill () {
    assertThat(client.kill())
        .as("EPMD client wasn't killed")
        .isTrue();
  }
}
