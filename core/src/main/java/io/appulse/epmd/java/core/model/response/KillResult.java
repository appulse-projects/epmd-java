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

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Locale.ENGLISH;

import java.util.stream.Stream;

import io.appulse.utils.Bytes;

import lombok.ToString;
import lombok.val;

/**
 * Kill EPMD result.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@ToString
public enum KillResult implements Response {

  /**
   * Success result.
   */
  OK,

  /**
   * Unsuccess result.
   */
  NOK;

  static KillResult from (Bytes bytes) {
    val string = bytes.readString(ISO_8859_1);
    return Stream.of(values())
        .filter(it -> it.name().equalsIgnoreCase(string))
        .findAny()
        .orElse(NOK);
  }

  @Override
  public byte[] toBytes () {
    return name().toUpperCase(ENGLISH).getBytes(ISO_8859_1);
  }
}
