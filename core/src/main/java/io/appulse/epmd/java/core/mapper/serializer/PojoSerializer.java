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

package io.appulse.epmd.java.core.mapper.serializer;

import static io.appulse.epmd.java.core.util.BytesUtil.align;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;

import io.appulse.epmd.java.core.mapper.MessageParser;

import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
class PojoSerializer implements Serializer {

  @Override
  public byte[] serialize (Object object, Class<?> type) throws Exception {
    val output = new ByteArrayOutputStream();
    for (val descriptor : MessageParser.parse(type)) {
      val field = type.getDeclaredField(descriptor.getName());
      AccessController.doPrivileged((PrivilegedAction<?>) () -> {
        field.setAccessible(true);
        return null;
      });

      val value = field.get(object);
      if (descriptor.isOptional() && isEmptyOptional(value)) {
        continue;
      }

      val bytes = descriptor.getSerializer().serialize(value);
      if (descriptor.isHasLengthBefore()) {
        val length = ByteBuffer.allocate(Integer.BYTES).putInt(bytes.length).array();
        output.write(align(length, descriptor.getLength()));
        output.write(bytes);
      } else if (descriptor.getLength() > 0) {
        output.write(align(bytes, descriptor.getLength()));
      } else {
        output.write(bytes);
      }
    }
    return output.toByteArray();
  }

  @Override
  public boolean isApplicable (Class<?> type) {
    return !type.isEnum();
  }

  private boolean isEmptyOptional (Object value) {
    return value == null || value instanceof Optional && !((Optional<?>) value).isPresent();
  }
}
