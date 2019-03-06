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
import static org.assertj.core.groups.Tuple.tuple;

import io.appulse.epmd.java.core.model.response.EpmdInfo.NodeDescription;
import io.appulse.utils.Bytes;

import lombok.val;
import org.junit.jupiter.api.Test;

class EpmdInfoTest {

  @Test
  void serializeEmpty () {
    val expected = Bytes.resizableArray()
        .write4B(8080)
        .array();

    val request = EpmdInfo.builder()
        .port(8080)
        .build();

    assertThat(request.toBytes())
        .isEqualTo(expected);
  }

  @Test
  void serializeNotEmpty () {
    val str = "name popa1 at port 1234\n" +
              "name popa2 at port 5678\n" +
              "name popa3 at port 9000";

    val expected = Bytes.resizableArray()
        .write4B(8080)
        .writeNB(str, ISO_8859_1)
        .array();

    val request = EpmdInfo.builder()
        .port(8080)
        .node(NodeDescription.builder()
            .name("popa1")
            .port(1234)
            .build()
        )
        .node(NodeDescription.builder()
            .name("popa2")
            .port(5678)
            .build()
        )
        .node(NodeDescription.builder()
            .name("popa3")
            .port(9000)
            .build()
        )
        .build();

    assertThat(request.toBytes())
        .isEqualTo(expected);
  }

  @Test
  void deserializeEmpty () {
    val bytes = Bytes.resizableArray()
        .write4B(8080)
        .array();

    val response = Response.parse(bytes, EpmdInfo.class);

    assertThat(response)
        .isNotNull();

    assertThat(response.getPort())
        .isEqualTo(8080);

    assertThat(response.getNodes())
        .isEmpty();
  }

  @Test
  void deserializeNotEmpty () {
    val str = "name popa1 at port 1234\n" +
              "name popa2 at port 5678\n" +
              "name popa3 at port 9000";

    val bytes = Bytes.resizableArray()
        .write4B(8080)
        .writeNB(str, ISO_8859_1)
        .array();

    val response = Response.parse(bytes, EpmdInfo.class);

    assertThat(response)
        .isNotNull();

    assertThat(response.getPort())
        .isEqualTo(8080);

    assertThat(response.getNodes())
        .extracting("name", "port")
        .contains(tuple("popa1", 1234),
                  tuple("popa2", 5678),
                  tuple("popa3", 9000));
  }
}
