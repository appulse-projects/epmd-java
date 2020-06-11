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
 * What epmd has been told, differs very much between versions, both
 * 111 and 110 seems to have been used to tell epmd, while
 * the actual node types has still been 104 and 109.
 * EPMD does not care about this, why we move back to using
 * the correct tag (an 'n') for all nodes.
 * <p>
 * see:
 * https://github.com/erlang/otp/blob/master/erts/epmd/epmd.mk#L36
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum NodeType {

  /**
   * R3 hidden node type.
   */
  R3_HIDDEN(72),

  /**
   * R3 node type.
   */
  R3_ERLANG(77),

  /**
   * R4 hidden node type.
   */
  R4_HIDDEN(104),

  /**
   * R4 node type.
   */
  R4_ERLANG(109),

  /**
   * R6 node type.
   */
  R6_ERLANG(110),

  /**
   * Unknown node type.
   */
  UNKNOWN(-1);

  @Getter
  byte code;

  NodeType (int code) {
    this.code = (byte) code;
  }

  /**
   * Parses numeric code to {@link NodeType} instance.
   *
   * @param code {@link NodeType} numeric representation.
   *
   * @return {@link NodeType} instance. {@link NodeType#UNKNOWN} if unknown.
   */
  public static NodeType of (byte code) {
    return Stream.of(values())
        .filter(it -> it.getCode() == code)
        .findAny()
        .orElse(UNKNOWN);
  }
}
