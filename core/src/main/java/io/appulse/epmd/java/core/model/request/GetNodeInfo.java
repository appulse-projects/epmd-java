/*
 * Copyright 2019 the original author or authors.
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

import static io.appulse.epmd.java.core.model.Tag.PORT_PLEASE2_REQUEST;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.utils.Bytes;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

/**
 * Get the distribution port of another Node.
 * <p>
 * When one node wants to connect to another node it starts with a this request to the EPMD
 * on the host where the node resides to get the distribution port that the node listens to.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@Value
@AllArgsConstructor
public class GetNodeInfo implements Request {

  @NonNull
  String name;

  GetNodeInfo (Bytes bytes) {
    name = bytes.readString(ISO_8859_1);
  }

  @Override
  public byte[] toBytes () {
    val nameBytes = name.getBytes(ISO_8859_1);
    val length = Byte.BYTES + nameBytes.length;
    return Bytes.allocate(length + Short.BYTES)
        .write2B(length)
        .write1B(getTag().getCode())
        .writeNB(nameBytes)
        .array();
  }

  @Override
  public Tag getTag () {
    return PORT_PLEASE2_REQUEST;
  }
}
