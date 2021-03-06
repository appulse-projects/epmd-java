/*
 * Copyright 2020 the original author or authors.
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

import io.appulse.utils.Bytes;

import lombok.val;
import org.junit.jupiter.api.Test;

class NodeInfoTest {

  @Test
  void serializeNok () {
    val expected = Bytes.resizableArray()
        .write1B(PORT2_RESPONSE.getCode())
        .write1B(1)
        .arrayCopy();

    val request = NodeInfo.builder()
        .ok(false)
        .build();

    assertThat(request.toBytes())
        .isEqualTo(expected);
  }

  @Test
  void serializeOk () {
    val name = "popa";
    val expected = Bytes.resizableArray()
        .write1B(PORT2_RESPONSE.getCode())
        .write1B(0)
        .write2B(8080)
        .write1B(104)
        .write1B(1)
        .write2B(1)
        .write2B(1)
        .write2B(name.getBytes().length)
        .writeNB(name)
        .write2B(0)
        .arrayCopy();

    val request = NodeInfo.builder()
        .ok(true)
        .port(8080)
        .type(R4_HIDDEN)
        .protocol(UDP)
        .high(R4)
        .low(R4)
        .name(name)
        .build();

    assertThat(request.toBytes())
        .isEqualTo(expected);
  }

  @Test
  void deserializeNok () {
    val bytes = Bytes.resizableArray()
        .write1B(PORT2_RESPONSE.getCode())
        .write1B(1)
        .arrayCopy();

    val response = Response.parse(bytes, NodeInfo.class);

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
  void deserializeOk () {
    val name = "popa";
    val bytes = Bytes.resizableArray()
        .write1B(PORT2_RESPONSE.getCode())
        .write1B(0)
        .write2B(8080)
        .write1B(104)
        .write1B(1)
        .write2B(1)
        .write2B(1)
        .write2B(name.getBytes().length)
        .writeNB(name, ISO_8859_1)
        .write2B(0)
        .arrayCopy();

    val response = Response.parse(bytes, NodeInfo.class);

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
