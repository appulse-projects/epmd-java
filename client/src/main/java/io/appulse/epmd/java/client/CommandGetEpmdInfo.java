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

import java.net.InetAddress;
import java.util.List;
import java.util.function.Supplier;

import io.appulse.epmd.java.core.model.request.GetEpmdInfo;
import io.appulse.epmd.java.core.model.response.EpmdInfo;
import io.appulse.epmd.java.core.model.response.EpmdInfo.NodeDescription;
import io.appulse.epmd.java.core.model.response.Response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

@Value
@Builder
class CommandGetEpmdInfo implements Supplier<List<NodeDescription>> {

  @NonNull
  ConnectionManager connectionManager;

  @NonNull
  InetAddress address;

  @NonNull
  Integer port;

  @Override
  public List<NodeDescription> get () {
    val requestBytes = new GetEpmdInfo().toBytes();
    try (val connection = connectionManager.connect(address, port)) {
      connection.send(requestBytes);
      val responseBytes = connection.receive();
      val response = Response.parse(responseBytes, EpmdInfo.class);
      return response.getNodes();
    }
  }
}
