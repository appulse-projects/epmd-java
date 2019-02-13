/*
 * Copyright 2019 the original author or authors.
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

package io.appulse.epmd.java.core.mapper;

import io.appulse.utils.Bytes;

/**
 * Interface API for specifying how to serialize/deserialize request/response.
 *
 * @since 0.0.2
 * @author Artem Labazin
 */
public interface DataSerializable {

  /**
   * Writes object instance to {@link Bytes}.
   *
   * @param bytes buffer for writing
   */
  void write (Bytes bytes);

  /**
   * Reads object instance from {@link Bytes}.
   *
   * @param bytes buffer for reading
   */
  void read (Bytes bytes);
}
