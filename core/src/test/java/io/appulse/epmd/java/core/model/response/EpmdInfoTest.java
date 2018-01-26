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

package io.appulse.epmd.java.core.model.response;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import io.appulse.epmd.java.core.mapper.deserializer.MessageDeserializer;
import io.appulse.epmd.java.core.mapper.serializer.MessageSerializer;
import io.appulse.epmd.java.core.model.response.EpmdInfo.NodeDescription;

import lombok.val;
import org.junit.Test;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public class EpmdInfoTest {

  @Test
  public void serializeEmpty () {
    val expected = ByteBuffer.allocate(Integer.BYTES)
        .putInt(8080)
        .array();

    val request = EpmdInfo.builder()
        .port(8080)
        .build();

    val bytes = new MessageSerializer().serialize(request);

    assertNotNull(bytes);
    assertArrayEquals(expected, bytes);
  }

  @Test
  public void serializeNotEmpty () {
    val str = "name popa1 at port 1234\n" +
              "name popa2 at port 5678\n" +
              "name popa3 at port 9000";

    val expected = ByteBuffer.allocate(Integer.BYTES + str.getBytes(ISO_8859_1).length)
        .putInt(8080)
        .put(str.getBytes(ISO_8859_1))
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

    val bytes = new MessageSerializer().serialize(request);

    assertNotNull(bytes);
    assertArrayEquals(expected, bytes);
  }

  @Test
  public void deserializeEmpty () {
    val bytes = ByteBuffer.allocate(Integer.BYTES)
        .putInt(8080)
        .array();

    val response = new MessageDeserializer().deserialize(bytes, EpmdInfo.class);

    assertNotNull(response);

    assertEquals(8080, response.getPort());

    assertNotNull(response.getNodes());
    assertTrue(response.getNodes().isEmpty());
  }

  @Test
  public void deserializeNotEmpty () {
    val str = "name popa1 at port 1234\n" +
              "name popa2 at port 5678\n" +
              "name popa3 at port 9000";

    val bytes = ByteBuffer.allocate(Integer.BYTES + str.getBytes(ISO_8859_1).length)
        .putInt(8080)
        .put(str.getBytes(ISO_8859_1))
        .array();

    val response = new MessageDeserializer().deserialize(bytes, EpmdInfo.class);

    assertNotNull(response);

    assertEquals(8080, response.getPort());

    assertNotNull(response.getNodes());
    assertFalse(response.getNodes().isEmpty());
    assertEquals(3, response.getNodes().size());

    val node1 = response.getNodes().get(0);
    assertEquals("popa1", node1.getName());
    assertEquals(1234, node1.getPort());

    val node2 = response.getNodes().get(1);
    assertEquals("popa2", node2.getName());
    assertEquals(5678, node2.getPort());

    val node3 = response.getNodes().get(2);
    assertEquals("popa3", node3.getName());
    assertEquals(9000, node3.getPort());
  }
}
