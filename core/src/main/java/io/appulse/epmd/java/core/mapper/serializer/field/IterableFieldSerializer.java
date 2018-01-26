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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import lombok.SneakyThrows;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public final class IterableFieldSerializer extends AbstractFieldSerializer<Object> {

  @Override
  @SneakyThrows
  public byte[] write (Object value) {
    val serializer = FieldSerializerCache.get(GuessMeFieldSerializer.class);
    try (val output = new ByteArrayOutputStream()) {

      if (value instanceof Iterable) {
        write(output, serializer, (Iterable<?>) value);
      } else if (value instanceof Object[]) {
        write(output, serializer, (Object[]) value);
      } else if (value instanceof char[]) {
        write(output, serializer, (char[]) value);
      } else if (value instanceof byte[]) {
        write(output, serializer, (byte[]) value);
      } else if (value instanceof short[]) {
        write(output, serializer, (short[]) value);
      } else if (value instanceof int[]) {
        write(output, serializer, (int[]) value);
      } else if (value instanceof long[]) {
        write(output, serializer, (long[]) value);
      } else if (value instanceof float[]) {
        write(output, serializer, (float[]) value);
      } else if (value instanceof double[]) {
        write(output, serializer, (double[]) value);
      } else {
        throw new UnsupportedOperationException();
      }

      return output.toByteArray();
    }
  }

  @Override
  boolean isApplicable (Object value) {
    return value.getClass().isArray() || value instanceof Iterable;
  }

  @SneakyThrows
  protected void write (OutputStream output, FieldSerializer<?> serializer, Iterable<?> iterable) {
    val iterator = iterable.iterator();
    if (!iterator.hasNext()) {
      return;
    }

    for (val item : iterable) {
      byte[] bytes = serializer.serialize(item);
      output.write(bytes);
    }
  }

  @SneakyThrows
  private void write (OutputStream output, FieldSerializer<?> serializer, Object[] array) {
    if (array.length == 0) {
      return;
    }

    for (val item : array) {
      byte[] bytes = serializer.serialize(item);
      output.write(bytes);
    }
  }

  @SneakyThrows
  private void write (OutputStream output, FieldSerializer<?> serializer, char[] array) {
    if (array.length == 0) {
      return;
    }

    for (val item : array) {
      byte[] bytes = serializer.serialize(item);
      output.write(bytes);
    }
  }

  @SneakyThrows
  private void write (OutputStream output, FieldSerializer<?> serializer, byte[] array) {
    if (array.length == 0) {
      return;
    }

    for (val item : array) {
      byte[] bytes = serializer.serialize(item);
      output.write(bytes);
    }
  }

  @SneakyThrows
  private void write (OutputStream output, FieldSerializer<?> serializer, short[] array) {
    if (array.length == 0) {
      return;
    }

    for (val item : array) {
      byte[] bytes = serializer.serialize(item);
      output.write(bytes);
    }
  }

  @SneakyThrows
  private void write (OutputStream output, FieldSerializer<?> serializer, int[] array) {
    if (array.length == 0) {
      return;
    }

    for (val item : array) {
      byte[] bytes = serializer.serialize(item);
      output.write(bytes);
    }
  }

  @SneakyThrows
  private void write (OutputStream output, FieldSerializer<?> serializer, long[] array) {
    if (array.length == 0) {
      return;
    }

    for (val item : array) {
      byte[] bytes = serializer.serialize(item);
      output.write(bytes);
    }
  }

  @SneakyThrows
  private void write (OutputStream output, FieldSerializer<?> serializer, float[] array) {
    if (array.length == 0) {
      return;
    }

    for (val item : array) {
      byte[] bytes = serializer.serialize(item);
      output.write(bytes);
    }
  }

  @SneakyThrows
  private void write (OutputStream output, FieldSerializer<?> serializer, double[] array) {
    if (array.length == 0) {
      return;
    }

    for (val item : array) {
      byte[] bytes = serializer.serialize(item);
      output.write(bytes);
    }
  }
}
