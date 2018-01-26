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

package io.appulse.epmd.java.core.model.request;

import static io.appulse.epmd.java.core.model.NodeType.R3_HIDDEN;
import static io.appulse.epmd.java.core.model.Protocol.SCTP;
import static io.appulse.epmd.java.core.model.Version.R6;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
public class RegistrationTest {

  @Test
  public void serialize () {
    val name = "popa";

    val expected = ByteBuffer.allocate(Short.BYTES + (13 + name.getBytes().length))
        .putShort((short) (13 + name.getBytes().length))
        .put((byte) 120)
        .putShort((short) 8080)
        .put(R3_HIDDEN.getCode())
        .put(SCTP.getCode())
        .putShort(R6.getCode())
        .putShort(R6.getCode())
        .putShort((short) name.getBytes().length)
        .put(name.getBytes())
        .putShort((short) 0)
        .array();

    val request = Registration.builder()
        .port(8080)
        .type(R3_HIDDEN)
        .protocol(SCTP)
        .high(R6)
        .low(R6)
        .name("popa")
        .extra(0)
        .build();

    val bytes = new MessageSerializer().serialize(request);

    assertNotNull(bytes);
    assertArrayEquals(expected, bytes);
  }

  @Test
  public void deserialize () {
    val name = "popa";

    val bytes = ByteBuffer.allocate(Short.BYTES + (13 + name.getBytes().length))
        .putShort((short) (13 + name.getBytes().length))
        .put((byte) 120)
        .putShort((short) 8080)
        .put(R3_HIDDEN.getCode())
        .put(SCTP.getCode())
        .putShort(R6.getCode())
        .putShort(R6.getCode())
        .putShort((short) name.getBytes().length)
        .put(name.getBytes())
        .putShort((short) 0)
        .array();

    val response = new MessageDeserializer().deserialize(bytes, Registration.class);

    assertNotNull(response);
    assertEquals(8080, response.getPort());
    assertEquals(R3_HIDDEN, response.getType());
    assertEquals(SCTP, response.getProtocol());
    assertEquals(R6, response.getHigh());
    assertEquals(R6, response.getLow());
    assertEquals(name, response.getName());
  }
}
