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

import static io.appulse.epmd.java.core.util.BytesUtil.asInteger;

import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

import io.appulse.epmd.java.core.mapper.MessageParser;

import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
class PojoDeserializer implements Deserializer {

  @Override
  public <T> T deserialize (ByteBuffer buffer, Class<T> type) throws Exception {
    T result = type.newInstance();
    for (val descriptor : MessageParser.parse(type)) {
      if (descriptor.isOptional() && buffer.remaining() == 0) {
        break;
      }

      val field = type.getDeclaredField(descriptor.getName());
      AccessController.doPrivileged((PrivilegedAction<?>) () -> {
        field.setAccessible(true);
        return null;
      });

      val length = descriptor.isHasLengthBefore()
             ? asInteger(buffer, descriptor.getLength())
             : descriptor.getLength() <= 0
               ? buffer.remaining()
               : descriptor.getLength();

      val fieldBytes = new byte[length];
      buffer.get(fieldBytes, 0, length);

      val value = descriptor.getDeserializer().read(fieldBytes, descriptor);
      field.set(result, value);
    }
    return result;
  }

  @Override
  public boolean isApplicable (Class<?> type) {
    return !type.isEnum();
  }
}
