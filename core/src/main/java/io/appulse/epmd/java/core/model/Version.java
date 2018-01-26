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
 * @author Artem Labazin
 * @since 0.0.1
 */
public enum Version {

  R3(0),
  R4(1),
  R5(2),
  R5C(3),
  R6_DEVELOPMENT(4),
  R6(5),
  UNDEFINED(-1);

  @Getter
  private final short code;

  Version (int code) {
    this.code = (short) code;
  }

  public static Version of (short code) {
    return Stream.of(values())
        .filter(it -> it.getCode() == code)
        .findAny()
        .orElse(UNDEFINED);
  }
}
