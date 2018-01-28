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

import static io.appulse.epmd.java.core.model.Tag.KILL_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

import io.appulse.epmd.java.core.mapper.deserializer.MessageDeserializer;
import io.appulse.epmd.java.core.mapper.serializer.MessageSerializer;
import io.appulse.utils.Bytes;

import lombok.val;
import org.junit.Test;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public class KillTest {

  @Test
  public void serialize () {
    val expected = Bytes.allocate()
        .put2B(1)
        .put1B(KILL_REQUEST.getCode())
        .array();

    val request = new Kill();
    assertThat(new MessageSerializer().serialize(request))
        .isEqualTo(expected);
  }

  @Test
  public void deserialize () {
    val bytes = Bytes.allocate()
        .put2B(1)
        .put1B(KILL_REQUEST.getCode())
        .array();

    assertThat(new MessageDeserializer().deserialize(bytes, Kill.class))
        .isNotNull()
        .isInstanceOf(Kill.class);
  }
}
