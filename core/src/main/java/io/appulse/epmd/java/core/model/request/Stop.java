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

package io.appulse.epmd.java.core.model.request;

import static io.appulse.epmd.java.core.model.Tag.STOP_REQUEST;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.core.mapper.ExpectedResponse;
import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.response.StopResult;
import io.appulse.utils.Bytes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Stop EPMD server request.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@ExpectedResponse(StopResult.class)
public class Stop implements Request {

  @NonNull
  String name;

  @Override
  public void write (@NonNull Bytes bytes) {
    bytes.put(name, ISO_8859_1);
  }

  @Override
  public void read (@NonNull Bytes bytes) {
    name = bytes.getString(ISO_8859_1);
  }

  @Override
  public Tag getTag () {
    return STOP_REQUEST;
  }
}
