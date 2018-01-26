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

package io.appulse.epmd.java.core.util;

import static java.lang.Math.min;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public final class BytesUtil {

  public static byte[] asBytes (char number) {
    return ByteBuffer.allocate(Character.BYTES).putChar(number).array();
  }

  public static byte[] asBytes (byte number) {
    return ByteBuffer.allocate(Byte.BYTES).put(number).array();
  }

  public static byte[] asBytes (short number) {
    return ByteBuffer.allocate(Short.BYTES).putShort(number).array();
  }

  public static byte[] asBytes (int number) {
    return ByteBuffer.allocate(Integer.BYTES).putInt(number).array();
  }

  public static byte[] asBytes (long number) {
    return ByteBuffer.allocate(Long.BYTES).putLong(number).array();
  }

  public static byte[] asBytes (float number) {
    return ByteBuffer.allocate(Float.BYTES).putFloat(number).array();
  }

  public static byte[] asBytes (double number) {
    return ByteBuffer.allocate(Double.BYTES).putDouble(number).array();
  }

  public static int asInteger (@NonNull ByteBuffer buffer, int length) {
    val bytes = new byte[length];
    buffer.get(bytes, 0, length);
    return asInteger(bytes);
  }

  public static int asInteger (@NonNull byte[] bytes) {
    val aligned = align(bytes, Integer.BYTES);
    return ByteBuffer.wrap(aligned).getInt();
  }

  public static byte[] concatenate (@NonNull byte[]... arrays) {
    val size = Stream.of(arrays)
        .mapToInt(it -> it.length)
        .sum();

    val buffer = ByteBuffer.allocate(size);
    Stream.of(arrays).forEach(buffer::put);
    return buffer.array();
  }

  public static byte[] align (@NonNull byte[] bytes, int length) {
    if (length <= 0) {
      return bytes;
    }

    val result = new byte[length];
    int srcPos = 0;
    int destPos = 0;

    if (bytes.length > length) {
      srcPos = bytes.length - length;
    } else if (bytes.length < length) {
      destPos = length - bytes.length;
    }

    System.arraycopy(bytes, srcPos, result, destPos, min(bytes.length, length));
    return result;
  }

  private BytesUtil () {
  }
}
