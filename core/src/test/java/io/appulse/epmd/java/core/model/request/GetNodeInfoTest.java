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
public class GetNodeInfoTest {

  @Test
  public void serialize () {
    val name = "popa";

    val expected = ByteBuffer.allocate(Short.BYTES + Byte.BYTES + name.getBytes().length)
        .putShort((short) (Byte.BYTES + name.getBytes().length))
        .put((byte) 122)
        .put(name.getBytes())
        .array();

    val request = new GetNodeInfo(name);

    val bytes = new MessageSerializer().serialize(request);

    assertNotNull(bytes);
    assertArrayEquals(expected, bytes);
  }

  @Test
  public void deserialize () {
    val name = "popa";

    val bytes = ByteBuffer.allocate(Short.BYTES + Byte.BYTES + name.getBytes().length)
        .putShort((short) (Byte.BYTES + name.getBytes().length))
        .put((byte) 122)
        .put(name.getBytes())
        .array();

    val response = new MessageDeserializer().deserialize(bytes, GetNodeInfo.class);

    assertNotNull(response);
    assertEquals(name, response.getName());
  }
}
