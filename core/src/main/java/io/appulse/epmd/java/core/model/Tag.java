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

import java.util.stream.Stream;

import lombok.Getter;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public enum Tag {

  UNDEFINED(-1),
  ALIVE2_REQUEST(120),
  ALIVE2_RESPONSE(121),
  PORT_PLEASE2_REQUEST(122),
  PORT2_RESPONSE(119),
  NAMES_REQUEST(110),
  DUMP_REQUEST(100),
  KILL_REQUEST(107),
  STOP_REQUEST(115);

  @Getter
  private final byte code;

  Tag (int code) {
    this.code = (byte) code;
  }

  public static Tag of (byte code) {
    return Stream.of(values())
            .filter(it -> it.getCode() == code)
            .findAny()
            .orElse(UNDEFINED);
  }
}
