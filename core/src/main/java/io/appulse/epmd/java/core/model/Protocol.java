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

package io.appulse.epmd.java.core.model;

import static lombok.AccessLevel.PRIVATE;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Protocol type.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum Protocol {

  /**
   * TCP protocol.
   */
  TCP(0),

  /**
   * UDP protocol.
   */
  UDP(1),

  /**
   * STCP protocol.
   */
  SCTP(2),

  /**
   * Unknown protocol.
   */
  UNKNOWN(-1);

  @Getter
  byte code;

  Protocol (int code) {
    this.code = (byte) code;
  }

  /**
   * Parses numeric code to {@link Protocol} instance.
   *
   * @param code {@link Protocol} numeric representation.
   *
   * @return {@link Protocol} instance. {@link Protocol#UNKNOWN} if unknown.
   */
  public static Protocol of (byte code) {
    return Stream.of(values())
        .filter(it -> it.getCode() == code)
        .findAny()
        .orElse(UNKNOWN);
  }
}
