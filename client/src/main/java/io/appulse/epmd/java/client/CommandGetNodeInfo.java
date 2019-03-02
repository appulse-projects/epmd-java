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

package io.appulse.epmd.java.client;

import static java.util.Optional.ofNullable;

import java.net.InetAddress;
import java.util.Optional;
import java.util.function.Supplier;

import io.appulse.epmd.java.core.model.request.GetNodeInfo;
import io.appulse.epmd.java.core.model.response.NodeInfo;
import io.appulse.epmd.java.core.model.response.Response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

@Value
@Builder
class CommandGetNodeInfo implements Supplier<Optional<NodeInfo>> {

  @NonNull
  ConnectionManager connectionManager;

  @NonNull
  InetAddress address;

  @NonNull
  Integer port;

  @NonNull
  String shortNodeName;

  @Override
  public Optional<NodeInfo> get () {
    val request = new GetNodeInfo(shortNodeName);
    val requestBytes = request.toBytes();
    try (val connection = connectionManager.connect(address, port)) {
      connection.send(requestBytes);
      val responseBytes = connection.receive();
      return ofNullable(responseBytes)
          .map(it -> Response.parse(it, NodeInfo.class))
          .filter(NodeInfo::isOk);
    }
  }
}
