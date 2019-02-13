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

package io.appulse.epmd.java.core.model.response;

import static io.appulse.epmd.java.core.model.NodeType.R4_HIDDEN;
import static io.appulse.epmd.java.core.model.Protocol.UDP;
import static io.appulse.epmd.java.core.model.Tag.PORT2_RESPONSE;
import static io.appulse.epmd.java.core.model.Version.R4;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.assertj.core.api.Assertions.assertThat;

import io.appulse.epmd.java.core.mapper.deserializer.MessageDeserializer;
import io.appulse.epmd.java.core.mapper.serializer.MessageSerializer;
import io.appulse.utils.Bytes;

import lombok.val;
import org.junit.Test;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public class NodeInfoTest {

  @Test
  public void serializeNok () {
    val expected = Bytes.allocate()
        .put1B(PORT2_RESPONSE.getCode())
        .put1B(1)
        .array();

    val request = NodeInfo.builder()
        .ok(false)
        .build();

    assertThat(new MessageSerializer().serialize(request))
        .isEqualTo(expected);
  }

  @Test
  public void serializeOk () {
    val name = "popa";
    val expected = Bytes.allocate()
        .put1B(PORT2_RESPONSE.getCode())
        .put1B(0)
        .put2B(8080)
        .put1B(104)
        .put1B(1)
        .put2B(1)
        .put2B(1)
        .put2B(name.getBytes().length)
        .put(name)
        .array();

    val request = NodeInfo.builder()
        .ok(true)
        .port(8080)
        .type(R4_HIDDEN)
        .protocol(UDP)
        .high(R4)
        .low(R4)
        .name(name)
        .build();

    assertThat(new MessageSerializer().serialize(request))
        .isEqualTo(expected);
  }

  @Test
  public void deserializeNok () {
    val bytes = Bytes.allocate()
        .put1B(PORT2_RESPONSE.getCode())
        .put1B(1)
        .array();

    val response = new MessageDeserializer().deserialize(bytes, NodeInfo.class);

    assertThat(response).isNotNull();
    assertThat(response.isOk()).isFalse();

    assertThat(response.getPort())
        .isNotPresent();

    assertThat(response.getType())
        .isNotPresent();

    assertThat(response.getProtocol())
        .isNotPresent();

    assertThat(response.getHigh())
        .isNotPresent();

    assertThat(response.getLow())
        .isNotPresent();

    assertThat(response.getName())
        .isNotPresent();
  }

  @Test
  public void deserializeOk () {
    val name = "popa";
    val bytes = Bytes.allocate()
        .put1B(PORT2_RESPONSE.getCode())
        .put1B(0)
        .put2B(8080)
        .put1B(104)
        .put1B(1)
        .put2B(1)
        .put2B(1)
        .put2B(name.getBytes().length)
        .put(name, ISO_8859_1)
        .array();

    val response = new MessageDeserializer().deserialize(bytes, NodeInfo.class);

    assertThat(response).isNotNull();
    assertThat(response.isOk()).isTrue();

    assertThat(response.getPort())
        .hasValue(8080);

    assertThat(response.getType())
        .hasValue(R4_HIDDEN);

    assertThat(response.getProtocol())
        .hasValue(UDP);

    assertThat(response.getHigh())
        .hasValue(R4);

    assertThat(response.getLow())
        .hasValue(R4);

    assertThat(response.getName())
        .hasValue(name);
  }
}
