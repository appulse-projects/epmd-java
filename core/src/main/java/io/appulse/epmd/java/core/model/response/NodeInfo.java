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

package io.appulse.epmd.java.core.model.response;

import static io.appulse.epmd.java.core.model.Tag.PORT2_RESPONSE;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.util.Optional;

import io.appulse.epmd.java.core.model.NodeType;
import io.appulse.epmd.java.core.model.Protocol;
import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.TaggedMessage;
import io.appulse.epmd.java.core.model.Version;
import io.appulse.utils.Bytes;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

/**
 * Node info response.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@Value
public class NodeInfo implements Response, TaggedMessage {

  boolean ok;

  Optional<Integer> port;

  Optional<NodeType> type;

  Optional<Protocol> protocol;

  Optional<Version> high;

  Optional<Version> low;

  Optional<String> name;

  Optional<byte[]> extra;

  /**
   * All arguments constructor.
   *
   * @param ok response success or not
   *
   * @param port node's port
   *
   * @param type node type
   *
   * @param protocol protocol version
   *
   * @param high node's highest supported version
   *
   * @param low node's lowest supported version
   *
   * @param name node's name
   *
   * @param extra node's extra info
   */
  @Builder
  public NodeInfo (@NonNull Boolean ok,
                   Integer port,
                   NodeType type,
                   Protocol protocol,
                   Version high,
                   Version low,
                   String name,
                   byte[] extra
  ) {
    this.ok = ok;
    this.port = ofNullable(port);
    this.type = ofNullable(type);
    this.protocol = ofNullable(protocol);
    this.high = ofNullable(high);
    this.low = ofNullable(low);
    this.name = ofNullable(name);
    this.extra = ofNullable(extra);
  }

  NodeInfo (Bytes bytes) {
    val tag = Tag.of(bytes.getByte());
    if (tag != getTag()) {
      throw new IllegalArgumentException("Unexpected message's tag " + tag.name());
    }

    if (bytes.getByte() != 0) {
      ok = false;
      port = empty();
      type = empty();
      protocol = empty();
      high = empty();
      low = empty();
      name = empty();
      extra = empty();
      return;
    }

    ok = true;
    port = of(bytes.getUnsignedShort());
    type = of(bytes.getByte()).map(NodeType::of);
    protocol = of(bytes.getByte()).map(Protocol::of);
    high = of(bytes.getUnsignedShort()).map(Version::of);
    low = of(bytes.getUnsignedShort()).map(Version::of);

    val length = bytes.getUnsignedShort();
    name = of(bytes.getString(length, ISO_8859_1));

    val extraLength = bytes.getUnsignedShort();
    extra = of(bytes.getBytes(extraLength));
  }

  @Override
  public byte[] toBytes () {
    if (!ok) {
      return new byte[] { getTag().getCode(), 1 };
    }

    val nameBytes = name
        .map(it -> it.getBytes(ISO_8859_1))
        .orElseGet(() -> new byte[0]);

    val extraBytes = extra
        .orElseGet(() -> new byte[0]);

    return Bytes.allocate(14 + nameBytes.length + extraBytes.length)
        .put1B(getTag().getCode())
        .put1B(0)
        .put2B(port.get())
        .put1B(type.get().getCode())
        .put1B(protocol.get().getCode())
        .put2B(high.get().getCode())
        .put2B(low.get().getCode())
        .put2B(nameBytes.length)
        .put(nameBytes)
        .put2B(extraBytes.length)
        .put(extraBytes)
        .array();
  }

  @Override
  public final Tag getTag () {
    return PORT2_RESPONSE;
  }
}
