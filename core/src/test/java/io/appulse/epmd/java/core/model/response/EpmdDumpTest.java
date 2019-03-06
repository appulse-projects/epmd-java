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

import static io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status.ACTIVE;
import static io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status.OLD_OR_UNUSED;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump;
import io.appulse.utils.Bytes;

import lombok.val;
import org.junit.jupiter.api.Test;

class EpmdDumpTest {

  @Test
  void serializeEmpty () {
    val expected = Bytes.resizableArray()
        .write4B(8080)
        .array();

    val request = EpmdDump.builder()
        .port(8080)
        .build();

    assertThat(request.toBytes())
        .isEqualTo(expected);
  }

  @Test
  void serializeNotEmpty () {
    val str = "active name\t<popa1> at port 1234, fd = 1\n" +
              "old/unused name\t<popa2> at port 5678, fd = 9\n" +
              "active name\t<popa3> at port 9000, fd = 7";

    val expected = Bytes.resizableArray()
        .write4B(8080)
        .writeNB(str, ISO_8859_1)
        .array();

    val request = EpmdDump.builder()
        .port(8080)
        .node(new NodeDump(ACTIVE, "popa1", 1234, 1))
        .node(new NodeDump(OLD_OR_UNUSED, "popa2", 5678, 9))
        .node(new NodeDump(ACTIVE, "popa3", 9000, 7))
        //        .node(NodeDump.builder()
        //            .name("popa1")
        //            .port(1234)
        //            .status(ACTIVE)
        //            .fileDescriptor(1)
        //            .build()
        //        )
        //        .node(NodeDump.builder()
        //            .name("popa2")
        //            .port(5678)
        //            .status(OLD_OR_UNUSED)
        //            .fileDescriptor(9)
        //            .build()
        //        )
        //        .node(NodeDump.builder()
        //            .name("popa3")
        //            .port(9000)
        //            .status(ACTIVE)
        //            .fileDescriptor(7)
        //            .build()
        //        )
        .build();

    assertThat(request.toBytes())
        .isEqualTo(expected);
  }

  @Test
  void deserializeEmpty () {
    val bytes = Bytes.resizableArray()
        .write4B(8080)
        .array();

    val response = Response.parse(bytes, EpmdDump.class);

    assertThat(response)
        .isNotNull();

    assertThat(response.getPort()).isEqualTo(8080);
    assertThat(response.getNodes())
            .isNotNull()
            .isEmpty();
  }

  @Test
  void deserializeNotEmpty () {
    val str = "active name\t<popa1> at port 1234, fd = 1\n" +
              "old/unused name\t<popa2> at port 5678, fd = 9\n" +
              "active name\t<popa3> at port 9000, fd = 7";

    val bytes = Bytes.resizableArray()
        .write4B(8080)
        .writeNB(str, ISO_8859_1)
        .array();

    val response = Response.parse(bytes, EpmdDump.class);

    assertThat(response)
        .isNotNull();

    assertThat(response.getPort())
        .isEqualTo(8080);

    assertThat(response.getNodes())
        .extracting("name", "port", "status", "fileDescriptor")
        .contains(tuple("popa1", 1234, ACTIVE, 1),
                  tuple("popa2", 5678, OLD_OR_UNUSED, 9),
                  tuple("popa3", 9000, ACTIVE, 7));
  }
}
