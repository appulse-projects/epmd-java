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
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import java.util.Optional;

import io.appulse.epmd.java.core.mapper.Field;
import io.appulse.epmd.java.core.mapper.LengthBefore;
import io.appulse.epmd.java.core.mapper.Message;
import io.appulse.epmd.java.core.model.NodeType;
import io.appulse.epmd.java.core.model.Protocol;
import io.appulse.epmd.java.core.model.Version;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@Getter
@ToString
@Message(value = PORT2_RESPONSE, lengthBytes = 0)
@FieldDefaults(level = PRIVATE)
public class NodeInfo {

  @Field(bytes = 1)
  boolean ok;

  @Field(bytes = 2)
  Optional<Integer> port;

  @Field(bytes = 1)
  Optional<NodeType> type;

  @Field(bytes = 1)
  Optional<Protocol> protocol;

  @Field(bytes = 2)
  Optional<Version> high;

  @Field(bytes = 2)
  Optional<Version> low;

  @LengthBefore(bytes = 2)
  @Field
  Optional<String> name;

  public NodeInfo () {
    port = empty();
    type = empty();
    protocol = empty();
    high = empty();
    low = empty();
    name = empty();
  }

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
}
