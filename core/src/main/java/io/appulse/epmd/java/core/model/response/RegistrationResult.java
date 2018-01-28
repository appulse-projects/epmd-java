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

package io.appulse.epmd.java.core.model.response;

import static io.appulse.epmd.java.core.model.Tag.ALIVE2_RESPONSE;
import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.core.mapper.Message;
import io.appulse.epmd.java.core.mapper.DataSerializable;
import io.appulse.utils.Bytes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Message(value = ALIVE2_RESPONSE)
@FieldDefaults(level = PRIVATE)
public class RegistrationResult implements DataSerializable {

  boolean ok;

  int creation;

  @Override
  public void write (@NonNull Bytes bytes) {
    if (!ok) {
      bytes.put1B(1);
      return;
    }

    bytes.put1B(0);
    bytes.put2B(creation);
  }

  @Override
  public void read (@NonNull Bytes bytes) {
    ok = bytes.getByte() == 0;

    if (ok) {
      creation = bytes.getInt();
    }
  }
}
