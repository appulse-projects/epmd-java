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

package io.appulse.epmd.java.core.mapper.serializer.field;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public final class NumberFieldSerializer extends AbstractFieldSerializer<Object> {

  @Override
  public byte[] write (Object value) {
    if (value instanceof Character) {
      return ByteBuffer.allocate(Character.BYTES).putChar((Character) value).array();
    } else if (value instanceof Byte) {
      return ByteBuffer.allocate(Byte.BYTES).put((Byte) value).array();
    } else if (value instanceof Short) {
      return ByteBuffer.allocate(Short.BYTES).putShort((Short) value).array();
    } else if (value instanceof Integer) {
      return ByteBuffer.allocate(Integer.BYTES).putInt((Integer) value).array();
    } else if (value instanceof Long) {
      return ByteBuffer.allocate(Long.BYTES).putLong((Long) value).array();
    } else if (value instanceof Float) {
      return ByteBuffer.allocate(Float.BYTES).putFloat((Float) value).array();
    } else if (value instanceof Double) {
      return ByteBuffer.allocate(Double.BYTES).putDouble((Double) value).array();
    } else if (value instanceof BigInteger) {
      return ((BigInteger) value).toByteArray();
    } else if (value instanceof BigDecimal) {
      return new byte[0];
    } else {
      throw new UnsupportedOperationException("Unsupported number type " + value.getClass());
    }
  }

  @Override
  boolean isApplicable (Object value) {
    return value instanceof Number || value instanceof Character;
  }
}
