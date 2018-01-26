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

package io.appulse.epmd.java.core.mapper.deserializer.field;

import static io.appulse.epmd.java.core.util.BytesUtil.align;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import io.appulse.epmd.java.core.mapper.FieldDescriptor;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public class NumberFieldDeserializer extends AbstractFieldDeserializer<Object> {

  @Override
  public Object read (byte[] bytes, FieldDescriptor descriptor) {
    Class<?> type = descriptor.getType();
    return read(bytes, type);
  }

  @Override
  boolean isApplicable (FieldDescriptor descriptor) {
    Class<?> type = descriptor.getType();
    return Number.class.isAssignableFrom(type) ||
           Character.class.isAssignableFrom(type) ||
           type.isPrimitive() && type != Boolean.TYPE;
  }

  protected final Object read (byte[] bytes, Class<?> type) {
    if (Character.class.isAssignableFrom(type) || type == Character.TYPE) {
      return ByteBuffer.wrap(align(bytes, Character.BYTES)).getChar();
    } else if (Byte.class.isAssignableFrom(type) || type == Byte.TYPE) {
      return bytes[bytes.length - 1];
    } else if (Short.class.isAssignableFrom(type) || type == Short.TYPE) {
      return ByteBuffer.wrap(align(bytes, Short.BYTES)).getShort();
    } else if (Integer.class.isAssignableFrom(type) || type == Integer.TYPE) {
      return ByteBuffer.wrap(align(bytes, Integer.BYTES)).getInt();
    } else if (Long.class.isAssignableFrom(type) || type == Long.TYPE) {
      return ByteBuffer.wrap(align(bytes, Long.BYTES)).getLong();
    } else if (Float.class.isAssignableFrom(type) || type == Float.TYPE) {
      return ByteBuffer.wrap(align(bytes, Float.BYTES)).getFloat();
    } else if (Double.class.isAssignableFrom(type) || type == Double.TYPE) {
      return ByteBuffer.wrap(align(bytes, Double.BYTES)).getDouble();
    } else if (BigInteger.class.isAssignableFrom(type)) {
      return new BigInteger(bytes);
    } else if (BigDecimal.class.isAssignableFrom(type)) {
      return null;
    } else {
      throw new UnsupportedOperationException("Unsupported number type " + type);
    }
  }
}
