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

import java.nio.ByteBuffer;

import io.appulse.epmd.java.core.mapper.FieldDescriptor;
import io.appulse.epmd.java.core.mapper.deserializer.field.EnumFieldDeserializer;
import io.appulse.epmd.java.core.mapper.deserializer.field.FieldDeserializerCache;

import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
class EnumDeserializer implements Deserializer {

  private static final EnumFieldDeserializer ENUM_FIELD_DESERIALIZER;

  static {
    ENUM_FIELD_DESERIALIZER = FieldDeserializerCache.get(EnumFieldDeserializer.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T deserialize (ByteBuffer buffer, Class<T> type) throws Exception {
    val length = buffer.remaining();
    val bytes = new byte[length];
    buffer.get(bytes, 0, length);
    val fakeDescriptor = FieldDescriptor.builder()
        .type(type)
        .build();
    return (T) ENUM_FIELD_DESERIALIZER.read(bytes, fakeDescriptor);
  }

  @Override
  public boolean isApplicable (Class<?> type) {
    return type.isEnum();
  }
}
