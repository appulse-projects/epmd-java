/*
 * Copyright 2020 the original author or authors.
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

import io.appulse.utils.Bytes;

import lombok.NonNull;
import lombok.val;

/**
 * Response message interface.
 */
public interface Response {

  /**
   * Parses a byte array into a specified type.
   *
   * @param array the byte array
   *
   * @param type the specified type, at which need to parse the array
   *
   * @param <T> parsed type, extends {@link Response}
   *
   * @return a parsed object
   */
  static <T extends Response> T parse (@NonNull byte[] array, Class<T> type) {
    val bytes = Bytes.wrap(array);
    return parse(bytes, type);
  }

  /**
   * Parses a byte array into a specified type.
   *
   * @param bytes the byte array
   *
   * @param type the specified type, at which need to parse the array
   *
   * @param <T> parsed type, extends {@link Response}
   *
   * @return a parsed object
   */
  @SuppressWarnings("unchecked")
  static <T extends Response> T parse (@NonNull Bytes bytes, @NonNull Class<T> type) {
    if (type == EpmdDump.class) {
      return (T) new EpmdDump(bytes);
    } else if (type == EpmdInfo.class) {
      return (T) new EpmdInfo(bytes);
    } else if (type == KillResult.class) {
      return (T) KillResult.from(bytes);
    } else if (type == NodeInfo.class) {
      return (T) new NodeInfo(bytes);
    } else if (type == RegistrationResult.class) {
      return (T) new RegistrationResult(bytes);
    } else if (type == StopResult.class) {
      return (T) StopResult.from(bytes);
    }
    throw new IllegalArgumentException();
  }

  /**
   * Converts the object into a byte array.
   *
   * @return the byte array
   */
  byte[] toBytes ();
}
