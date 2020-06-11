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

package io.appulse.epmd.java.core.model.request;

import static io.appulse.epmd.java.core.model.Tag.ALIVE2_REQUEST;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

import io.appulse.epmd.java.core.model.NodeType;
import io.appulse.epmd.java.core.model.Protocol;
import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.Version;
import io.appulse.utils.Bytes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

/**
 * Register a node in EPMD request.
 * <p>
 * When a distributed node is started it registers itself in the EPMD.
 * The message ALIVE2_REQ described below is sent from the node to the EPMD.
 * The response from the EPMD is ALIVE2_RESP.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@Value
@Builder
@AllArgsConstructor
public class Registration implements Request {

  /**
   * The port number on which the node accept connection requests.
   */
  int port;

  @NonNull
  NodeType type;

  @NonNull
  Protocol protocol;

  /**
   * The highest distribution version that this node can handle.
   */
  @NonNull
  Version high;

  /**
   * The lowest distribution version that this node can handle.
   */
  @NonNull
  Version low;

  /**
   * The node name as an UTF-8 encoded string.
   */
  @NonNull
  String name;

  @NonNull
  @Builder.Default
  byte[] extra = new byte[0];

  Registration (Bytes bytes) {
    port = bytes.readUnsignedShort();
    type = NodeType.of(bytes.readByte());
    protocol = Protocol.of(bytes.readByte());
    high = Version.of(bytes.readUnsignedShort());
    low = Version.of(bytes.readUnsignedShort());

    val nameLength = bytes.readUnsignedShort();
    name = bytes.readString(nameLength, ISO_8859_1);

    val extraLength = bytes.readUnsignedShort();
    extra = bytes.readBytes(extraLength);
  }

  @Override
  public byte[] toBytes () {
    val nameBytes = name.getBytes(ISO_8859_1);
    val length = 13 + nameBytes.length + extra.length;
    return Bytes.allocate(length + Short.BYTES)
        .write2B(length)
        .write1B(getTag().getCode())
        .write2B(port)
        .write1B(type.getCode())
        .write1B(protocol.getCode())
        .write2B(high.getCode())
        .write2B(low.getCode())
        .write2B(nameBytes.length)
        .writeNB(nameBytes)
        .write2B(extra.length)
        .writeNB(extra)
        .array();
  }

  @Override
  public Tag getTag () {
    return ALIVE2_REQUEST;
  }
}
