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

package io.appulse.epmd.java.core.model.request;

import static io.appulse.epmd.java.core.model.NodeType.R3_HIDDEN;
import static io.appulse.epmd.java.core.model.Protocol.SCTP;
import static io.appulse.epmd.java.core.model.Tag.ALIVE2_REQUEST;
import static io.appulse.epmd.java.core.model.Version.R6;
import static org.assertj.core.api.Assertions.assertThat;

import io.appulse.utils.Bytes;

import lombok.val;
import org.junit.jupiter.api.Test;

class RegistrationTest {

  @Test
  void serialize () {
    val name = "popa";
    val expected = Bytes.allocate()
        .put2B(13 + name.getBytes().length)
        .put1B(ALIVE2_REQUEST.getCode())
        .put2B(8080)
        .put1B(R3_HIDDEN.getCode())
        .put1B(SCTP.getCode())
        .put2B(R6.getCode())
        .put2B(R6.getCode())
        .put2B(name.getBytes().length)
        .put(name)
        .put2B(0)
        .array();

    val request = Registration.builder()
        .port(8080)
        .type(R3_HIDDEN)
        .protocol(SCTP)
        .high(R6)
        .low(R6)
        .name("popa")
        .build();

    assertThat(request.toBytes())
        .isEqualTo(expected);
  }

  @Test
  void deserialize () {
    val name = "popa";
    val bytes = Bytes.allocate()
        .put2B(13 + name.getBytes().length)
        .put1B(ALIVE2_REQUEST.getCode())
        .put2B(8080)
        .put1B(R3_HIDDEN.getCode())
        .put1B(SCTP.getCode())
        .put2B(R6.getCode())
        .put2B(R6.getCode())
        .put2B(name.getBytes().length)
        .put(name)
        .put2B(0)
        .array();

    val response = (Registration) Request.parse(bytes);

    assertThat(response)
        .isNotNull();

    assertThat(response.getPort())
        .isEqualTo(8080);

    assertThat(response.getType())
        .isEqualTo(R3_HIDDEN);

    assertThat(response.getProtocol())
        .isEqualTo(SCTP);

    assertThat(response.getHigh())
        .isEqualTo(R6);

    assertThat(response.getLow())
        .isEqualTo(R6);

    assertThat(response.getName())
        .isEqualTo(name);
  }
}
