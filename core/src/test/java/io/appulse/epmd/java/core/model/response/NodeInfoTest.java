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

import static io.appulse.epmd.java.core.model.NodeType.R4_HIDDEN;
import static io.appulse.epmd.java.core.model.Protocol.UDP;
import static io.appulse.epmd.java.core.model.Version.R4;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import io.appulse.epmd.java.core.mapper.deserializer.MessageDeserializer;
import io.appulse.epmd.java.core.mapper.serializer.MessageSerializer;

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
    val expected = ByteBuffer.allocate(Byte.BYTES + 1)
        .put((byte) 119)
        .put((byte) 1)
        .array();

    val request = NodeInfo.builder()
        .ok(false)
        .build();

    val bytes = new MessageSerializer().serialize(request);

    assertNotNull(bytes);
    assertArrayEquals(expected, bytes);
  }

  @Test
  public void serializeOk () {
    val name = "popa";

    val expected = ByteBuffer.allocate(Byte.BYTES + 11 + name.getBytes().length)
        .put((byte) 119)
        .put((byte) 0)
        .putShort((short) 8080)
        .put((byte) 104)
        .put((byte) 1)
        .putShort((short) 1)
        .putShort((short) 1)
        .putShort((short) name.getBytes().length)
        .put(name.getBytes())
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

    val bytes = new MessageSerializer().serialize(request);

    assertNotNull(bytes);
    assertArrayEquals(expected, bytes);
  }

  @Test
  public void deserializeNok () {
    val bytes = ByteBuffer.allocate(Byte.BYTES + 1)
        .put((byte) 119)
        .put((byte) 1)
        .array();

    val response = new MessageDeserializer().deserialize(bytes, NodeInfo.class);

    assertNotNull(response);
    assertEquals(false, response.isOk());
    assertFalse(response.getPort().isPresent());
    assertFalse(response.getType().isPresent());
    assertFalse(response.getProtocol().isPresent());
    assertFalse(response.getHigh().isPresent());
    assertFalse(response.getLow().isPresent());
    assertFalse(response.getName().isPresent());
  }

  @Test
  public void deserializeOk () {
    val name = "popa";

    val bytes = ByteBuffer.allocate(Byte.BYTES + 11 + name.getBytes().length)
        .put((byte) 119)
        .put((byte) 0)
        .putShort((short) 8080)
        .put((byte) 104)
        .put((byte) 1)
        .putShort((short) 1)
        .putShort((short) 1)
        .putShort((short) name.getBytes().length)
        .put(name.getBytes())
        .array();

    val response = new MessageDeserializer().deserialize(bytes, NodeInfo.class);

    assertNotNull(response);
    assertEquals(true, response.isOk());

    assertTrue(response.getPort().isPresent());
    assertEquals(8080, (int) response.getPort().get());

    assertTrue(response.getType().isPresent());
    assertEquals(R4_HIDDEN, response.getType().get());

    assertTrue(response.getProtocol().isPresent());
    assertEquals(UDP, response.getProtocol().get());

    assertTrue(response.getHigh().isPresent());
    assertEquals(R4, response.getHigh().get());

    assertTrue(response.getLow().isPresent());
    assertEquals(R4, response.getLow().get());

    assertTrue(response.getName().isPresent());
    assertEquals(name, response.getName().get());
  }
}
