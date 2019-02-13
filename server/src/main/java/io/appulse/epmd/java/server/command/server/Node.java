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

package io.appulse.epmd.java.server.command.server;

import io.appulse.epmd.java.core.model.NodeType;
import io.appulse.epmd.java.core.model.Protocol;
import io.appulse.epmd.java.core.model.Version;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Class for representing registered node.
 *
 * @since 0.3.2
 * @author Artem Labazin
 */
@Value
@Builder
public class Node {

  @NonNull
  String name;

  int port;

  @NonNull
  NodeType type;

  @NonNull
  Protocol protocol;

  @NonNull
  Version high;

  @NonNull
  Version low;

  int creation;
}
