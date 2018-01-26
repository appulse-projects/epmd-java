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

import static io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status.ACTIVE;
import static io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status.OLD_OR_UNUSED;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import io.appulse.epmd.java.core.mapper.deserializer.MessageDeserializer;
import io.appulse.epmd.java.core.mapper.serializer.MessageSerializer;
import io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump;

import lombok.val;
import org.junit.Test;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public class EpmdDumpTest {

  @Test
  public void serializeEmpty () {
    val expected = ByteBuffer.allocate(Integer.BYTES)
        .putInt(8080)
        .array();

    val request = EpmdDump.builder()
        .port(8080)
        .build();

    val bytes = new MessageSerializer().serialize(request);

    assertNotNull(bytes);
    assertArrayEquals(expected, bytes);
  }

  @Test
  public void serializeNotEmpty () {
    val str = "active name\t<popa1> at port 1234, fd = 1\n" +
              "old/unused name\t<popa2> at port 5678, fd = 9\n" +
              "active name\t<popa3> at port 9000, fd = 7";

    val expected = ByteBuffer.allocate(Integer.BYTES + str.getBytes(ISO_8859_1).length)
        .putInt(8080)
        .put(str.getBytes(ISO_8859_1))
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

    val bytes = new MessageSerializer().serialize(request);

    assertNotNull(bytes);
    assertArrayEquals(expected, bytes);
  }

  @Test
  public void deserializeEmpty () {
    val bytes = ByteBuffer.allocate(Integer.BYTES)
        .putInt(8080)
        .array();

    val response = new MessageDeserializer().deserialize(bytes, EpmdDump.class);

    assertNotNull(response);

    assertEquals(8080, response.getPort());

    assertNotNull(response.getNodes());
    assertTrue(response.getNodes().isEmpty());
  }

  @Test
  public void deserializeNotEmpty () {
    val str = "active name\t<popa1> at port 1234, fd = 1\n" +
              "old/unused name\t<popa2> at port 5678, fd = 9\n" +
              "active name\t<popa3> at port 9000, fd = 7";

    val bytes = ByteBuffer.allocate(Integer.BYTES + str.getBytes(ISO_8859_1).length)
        .putInt(8080)
        .put(str.getBytes(ISO_8859_1))
        .array();

    val response = new MessageDeserializer().deserialize(bytes, EpmdDump.class);

    assertNotNull(response);

    assertEquals(8080, response.getPort());

    assertNotNull(response.getNodes());
    assertFalse(response.getNodes().isEmpty());
    assertEquals(3, response.getNodes().size());

    val node1 = response.getNodes().get(0);
    assertEquals("popa1", node1.getName());
    assertEquals(1234, node1.getPort());
    assertEquals(ACTIVE, node1.getStatus());
    assertEquals(1, node1.getFileDescriptor());

    val node2 = response.getNodes().get(1);
    assertEquals("popa2", node2.getName());
    assertEquals(5678, node2.getPort());
    assertEquals(OLD_OR_UNUSED, node2.getStatus());
    assertEquals(9, node2.getFileDescriptor());

    val node3 = response.getNodes().get(2);
    assertEquals("popa3", node3.getName());
    assertEquals(9000, node3.getPort());
    assertEquals(ACTIVE, node3.getStatus());
    assertEquals(7, node3.getFileDescriptor());
  }
}
