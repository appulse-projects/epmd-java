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

package io.appulse.epmd.java.core.mapper.deserializer;

import static io.appulse.epmd.java.core.model.Tag.UNDEFINED;
import static io.appulse.epmd.java.core.util.BytesUtil.asInteger;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.nio.ByteBuffer;
import java.util.List;

import io.appulse.epmd.java.core.mapper.Message;
import io.appulse.epmd.java.core.model.Tag;

import lombok.SneakyThrows;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public final class MessageDeserializer {

  private static final List<Deserializer> DESERIALIZERS;

  static {
    DESERIALIZERS = asList(
        new PojoDeserializer(),
        new EnumDeserializer()
    );
  }

  @SneakyThrows
  public <T> T deserialize (byte[] bytes, Class<T> type) {
    val buffer = ofNullable(bytes)
        .map(ByteBuffer::wrap)
        .orElseThrow(RuntimeException::new);

    val annotation = ofNullable(type)
        .map(it -> it.getAnnotation(Message.class))
        .orElseThrow(RuntimeException::new);

    if (annotation.lengthBytes() > 0 && asInteger(buffer, annotation.lengthBytes()) != buffer.remaining()) {
      throw new RuntimeException();
    }
    if (annotation.value() != UNDEFINED && annotation.value() != Tag.of(buffer.get())) {
      throw new RuntimeException();
    }

    return DESERIALIZERS.stream()
        .filter(it -> it.isApplicable(type))
        .findAny()
        .orElseThrow(RuntimeException::new)
        .deserialize(buffer, type);
  }
}
