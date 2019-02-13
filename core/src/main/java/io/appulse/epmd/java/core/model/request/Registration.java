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

import static io.appulse.epmd.java.core.model.Tag.ALIVE2_REQUEST;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.core.mapper.ExpectedResponse;
import io.appulse.epmd.java.core.model.NodeType;
import io.appulse.epmd.java.core.model.Protocol;
import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.Version;
import io.appulse.epmd.java.core.model.response.RegistrationResult;
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
 * Register a node in EPMD request.
 * <p>
 * When a distributed node is started it registers itself in the EPMD.
 * The message ALIVE2_REQ described below is sent from the node to the EPMD.
 * The response from the EPMD is ALIVE2_RESP.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@ExpectedResponse(RegistrationResult.class)
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

  @Override
  public void write (@NonNull Bytes bytes) {
    bytes
        .put2B(port)
        .put1B(type.getCode())
        .put1B(protocol.getCode())
        .put2B(high.getCode())
        .put2B(low.getCode())
        .put2B(name.length())
        .put(name)
        .put2B(0);
  }

  @Override
  public void read (@NonNull Bytes bytes) {
    port = bytes.getUnsignedShort();
    type = NodeType.of(bytes.getByte());
    protocol = Protocol.of(bytes.getByte());
    high = Version.of(bytes.getShort());
    low = Version.of(bytes.getShort());
    val length = bytes.getShort();
    name = bytes.getString(length, ISO_8859_1);
  }

  @Override
  public Tag getTag () {
    return ALIVE2_REQUEST;
  }
}
