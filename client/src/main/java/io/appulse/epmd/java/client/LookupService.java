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

package io.appulse.epmd.java.client;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import java.net.InetAddress;
import java.util.Optional;

import io.appulse.epmd.java.core.model.request.GetNodeInfo;
import io.appulse.epmd.java.core.model.response.NodeInfo;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.2.2
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class LookupService {

  @NonNull
  InetAddress defaultAddress;

  int defaultPort;

  public Optional<NodeInfo> lookup (@NonNull String node) {
    return lookup(node, defaultPort);
  }

  @SneakyThrows
  public Optional<NodeInfo> lookup (@NonNull String node, int port) {
    val tokens = node.split("@", 2);
    val shortName = tokens[0];
    val address = tokens.length == 2
                  ? InetAddress.getByName(tokens[1])
                  : defaultAddress;

    return lookup(shortName, address, port);
  }

  public Optional<NodeInfo> lookup (@NonNull String node, @NonNull InetAddress address) {
    return lookup(node, address, defaultPort);
  }

  public Optional<NodeInfo> lookup (@NonNull String node, @NonNull InetAddress address, int port) {
    val tokens = node.split("@", 2);
    val shortName = tokens[0];

    log.debug("Looking up node '{}' at '{}:{}'", shortName, address, port);

    val request = new GetNodeInfo(shortName);
    try (val connection = new Connection(address, port)) {
      val response = connection.send(request, NodeInfo.class);
      log.debug("Lookup result is {}", response);
      return ofNullable(response)
          .filter(NodeInfo::isOk);
    }
  }
}
