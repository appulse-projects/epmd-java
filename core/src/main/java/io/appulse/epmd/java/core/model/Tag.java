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

package io.appulse.epmd.java.core.model;

import static lombok.AccessLevel.PRIVATE;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Message tags.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum Tag {

  /**
   * Register a node in EPMD request tag.
   */
  ALIVE2_REQUEST(120),

  /**
   * Registration response tag.
   */
  ALIVE2_RESPONSE(121),

  /**
   * Get the distribution port of another node request tag.
   */
  PORT_PLEASE2_REQUEST(122),

  /**
   * Node info from EPMD response tag.
   */
  PORT2_RESPONSE(119),

  /**
   * Get all registered names from EPMD request tag.
   */
  NAMES_REQUEST(110),

  /**
   * Dump all data from EPMD request tag.
   */
  DUMP_REQUEST(100),

  /**
   * Kill EPMD server request tag.
   */
  KILL_REQUEST(107),

  /**
   * Stop a node request tag.
   */
  STOP_REQUEST(115),

  /**
   * Unknown request tag.
   */
  UNKNOWN(-1);

  @Getter
  byte code;

  Tag (int code) {
    this.code = (byte) code;
  }

  /**
   * Parses numeric code to {@link Tag} instance.
   *
   * @param code {@link Tag} numeric representation.
   *
   * @return {@link Tag} instance. {@link Tag#UNKNOWN} if unknown.
   */
  public static Tag of (byte code) {
    return Stream.of(values())
            .filter(it -> it.getCode() == code)
            .findAny()
            .orElse(UNKNOWN);
  }
}
