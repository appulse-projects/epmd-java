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

import static io.appulse.epmd.java.core.model.Tag.ALIVE2_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;

import io.appulse.utils.Bytes;

import lombok.val;
import org.junit.jupiter.api.Test;

class RegistrationResultTest {

  @Test
  void serialize () {
    val expected = Bytes.allocate()
        .write1B(ALIVE2_RESPONSE.getCode())
        .write1B(0)
        .write2B(42)
        .array();

    val request = RegistrationResult.builder()
        .ok(true)
        .creation(42)
        .build();

    assertThat(request.toBytes())
        .isEqualTo(expected);
  }

  @Test
  void deserialize () {
    val bytes = Bytes.allocate()
        .write1B(ALIVE2_RESPONSE.getCode())
        .write1B(0)
        .write2B(42)
        .array();

    val response = Response.parse(bytes, RegistrationResult.class);

    assertThat(response)
        .isNotNull();

    assertThat(response.isOk())
        .isTrue();

    assertThat(response.getCreation())
        .isEqualTo(42);
  }
}
