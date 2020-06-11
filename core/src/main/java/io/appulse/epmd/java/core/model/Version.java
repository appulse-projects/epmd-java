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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Lowest/Highest supported version of the distribution protocol.
 * <p>
 * There was no protocol change in release R5, so we didn't need to raise
 * the version number. But now that R5A is released, it's best to keep it
 * this way.
 * <p>
 * The number was inadvertently raised for R5C, so we increase it again
 * for R6.
 * <p>
 * Distribution version 4 means a) distributed monitor and b) larger references
 * in the distribution format.
 * <p>
 * In format 5, nodes can explicitly tell each other which of the above
 * mentioned capabilities they can handle.
 * <p>
 * Distribution format 5 contains the new md5 based handshake.
 * <p>
 * see:
 * https://github.com/erlang/otp/blob/master/erts/epmd/epmd.mk#L51
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum Version {

  /**
   * R3 distribution protocol version.
   * <p>
   * Description:
   * <p>
   * http://erlang.org/doc/apps/erts/erl_dist_protocol.html#id106278
   */
  R3(0),

  /**
   * R4 distribution protocol version.
   * <p>
   * Description:
   * <p>
   * http://erlang.org/doc/apps/erts/erl_dist_protocol.html#id106750
   */
  R4(1),

  /**
   * R5 distribution protocol version.
   * <p>
   * Description:
   * <p>
   * http://erlang.org/doc/apps/erts/erl_dist_protocol.html#id106843
   */
  R5(2),

  /**
   * R5C distribution protocol version.
   * <p>
   * Description:
   * <p>
   * http://erlang.org/doc/apps/erts/erl_dist_protocol.html#id106857
   */
  R5C(3),

  /**
   * R6_DEVELOPMENT distribution protocol version.
   * <p>
   * Description:
   * <p>
   http://erlang.org/doc/apps/erts/erl_dist_protocol.html#id106869
   */
  R6_DEVELOPMENT(4),

  /**
   * R6 distribution protocol version.
   * <p>
   * Description:
   * <p>
   * http://erlang.org/doc/apps/erts/erl_dist_protocol.html#id106869
   */
  R6(5),

  /**
   * Unknown distribution protocol version.
   */
  UNKNOWN(Integer.MAX_VALUE);

  @Getter
  int code;

  /**
   * Parses numeric code to {@link Version} instance.
   *
   * @param code {@link Version} numeric representation.
   *
   * @return {@link Version} instance. {@link Version#UNKNOWN} if unknown.
   */
  public static Version of (int code) {
    return Stream.of(values())
        .filter(it -> it.getCode() == code)
        .findAny()
        .orElse(UNKNOWN);
  }
}
