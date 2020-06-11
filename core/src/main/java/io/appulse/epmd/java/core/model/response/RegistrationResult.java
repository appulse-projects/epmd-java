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

import static io.appulse.epmd.java.core.model.Tag.ALIVE2_RESPONSE;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.TaggedMessage;
import io.appulse.utils.Bytes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.val;

/**
 * Registration response.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@Value
@Builder
@AllArgsConstructor
public class RegistrationResult implements Response, TaggedMessage {

  boolean ok;

  int creation;

  RegistrationResult (Bytes bytes) {
    val tag = Tag.of(bytes.readByte());
    if (tag != getTag()) {
      throw new IllegalArgumentException("Unexpected message's tag " + tag.name());
    }

    ok = bytes.readByte() == 0;
    creation = ok
               ? bytes.readUnsignedShort()
               : 0;
  }

  @Override
  public byte[] toBytes () {
    val bytes = Bytes.allocate(4)
        .write1B(getTag().getCode());

    if (ok) {
      bytes.write1B(0);
    } else {
      bytes.write1B(1);
    }
    return bytes
        .write2B(creation)
        .array();
  }

  @Override
  public final Tag getTag () {
    return ALIVE2_RESPONSE;
  }
}
