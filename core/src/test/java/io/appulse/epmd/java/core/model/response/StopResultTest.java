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
public class StopResultTest {

  @Test
  public void serialize () {
    val value = StopResult.STOPPED;

    val bytes = ByteBuffer.allocate(value.name().getBytes(ISO_8859_1).length)
        .put(value.name().getBytes(ISO_8859_1))
        .array();

    val result = new MessageSerializer().serialize(value);

    assertNotNull(result);
    assertArrayEquals(bytes, result);
  }

  @Test
  public void deserialize () {
    val value = StopResult.STOPPED;

    val bytes = ByteBuffer.allocate(value.name().getBytes(ISO_8859_1).length)
        .put(value.name().getBytes(ISO_8859_1))
        .array();

    val result = new MessageDeserializer().deserialize(bytes, StopResult.class);
    assertNotNull(result);
    assertEquals(value, result);
  }
}
