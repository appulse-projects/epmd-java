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

import static io.appulse.epmd.java.core.model.Tag.PORT2_RESPONSE;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import java.util.Optional;

import io.appulse.epmd.java.core.model.NodeType;
import io.appulse.epmd.java.core.model.Protocol;
import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.TaggedMessage;
import io.appulse.epmd.java.core.model.Version;
import io.appulse.utils.Bytes;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.val;

/**
 * Node info response.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@Getter
@ToString
@FieldDefaults(level = PRIVATE)
public class NodeInfo implements TaggedMessage {

  boolean ok;

  Optional<Integer> port;

  Optional<NodeType> type;

  Optional<Protocol> protocol;

  Optional<Version> high;

  Optional<Version> low;

  Optional<String> name;

  /**
   * Default no arguments constructor with default values.
   */
  public NodeInfo () {
    port = empty();
    type = empty();
    protocol = empty();
    high = empty();
    low = empty();
    name = empty();
  }

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
   */
  @Builder
  public NodeInfo (boolean ok, Integer port, NodeType type, Protocol protocol, Version high, Version low, String name) {
    this.ok = ok;
    this.port = ofNullable(port);
    this.type = ofNullable(type);
    this.protocol = ofNullable(protocol);
    this.high = ofNullable(high);
    this.low = ofNullable(low);
    this.name = ofNullable(name);
  }

  @Override
  public void write (@NonNull Bytes bytes) {
    if (!ok) {
      bytes.put1B(1);
      return;
    }

    bytes.put1B(0);
    port.ifPresent(bytes::put2B);
    type.ifPresent(it -> bytes.put1B(it.getCode()));
    protocol.ifPresent(it -> bytes.put1B(it.getCode()));
    high.ifPresent(it -> bytes.put2B(it.getCode()));
    low.ifPresent(it -> bytes.put2B(it.getCode()));
    name.ifPresent(it -> {
      bytes.put2B(it.length());
      bytes.put(it, ISO_8859_1);
    });
  }

  @Override
  public void read (@NonNull Bytes bytes) {
    if (bytes.getByte() != 0) {
      ok = false;
      return;
    }

    ok = true;
    port = of(bytes.getUnsignedShort());
    type = of(bytes.getByte()).map(NodeType::of);
    protocol = of(bytes.getByte()).map(Protocol::of);
    high = of(bytes.getShort()).map(Version::of);
    low = of(bytes.getShort()).map(Version::of);

    val length = bytes.getShort();
    name = of(bytes.getString(length, ISO_8859_1));
  }

  @Override
  public Tag getTag () {
    return PORT2_RESPONSE;
  }
}
