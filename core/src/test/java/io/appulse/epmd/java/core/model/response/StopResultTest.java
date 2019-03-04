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

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.assertj.core.api.Assertions.assertThat;

import io.appulse.utils.Bytes;

import lombok.val;
import org.junit.jupiter.api.Test;

class StopResultTest {

  @Test
  void serialize () {
    val value = StopResult.STOPPED;

    val bytes = Bytes.allocate()
        .writeNB(value.name(), ISO_8859_1)
        .array();

    assertThat(value.toBytes())
        .isEqualTo(bytes);
  }

  @Test
  void deserialize () {
    val value = StopResult.STOPPED;

    val bytes = Bytes.allocate()
        .writeNB(value.name(), ISO_8859_1)
        .array();

    assertThat(Response.parse(bytes, StopResult.class))
        .isEqualTo(value);
  }
}
