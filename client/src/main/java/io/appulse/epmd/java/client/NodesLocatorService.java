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

import static lombok.AccessLevel.PRIVATE;

import java.net.InetAddress;
import java.util.List;

import io.appulse.epmd.java.core.model.request.GetEpmdInfo;
import io.appulse.epmd.java.core.model.response.EpmdInfo;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Service for retrieving node descriptions at EPMD server.
 *
 * @since 0.2.2
 * @author Artem Labazin
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class NodesLocatorService {

  @NonNull
  InetAddress defaultAddress;

  @NonNull
  Integer defaultPort;

  public List<EpmdInfo.NodeDescription> getNodes () {
    return getNodes(defaultAddress, defaultPort);
  }

  public List<EpmdInfo.NodeDescription> getNodes (int port) {
    return getNodes(defaultAddress, port);
  }

  public List<EpmdInfo.NodeDescription> getNodes (@NonNull String host) {
    return getNodes(host, defaultPort);
  }

  @SneakyThrows
  public List<EpmdInfo.NodeDescription> getNodes (@NonNull String host, int port) {
    val address = InetAddress.getByName(host);
    return getNodes(address, port);
  }

  public List<EpmdInfo.NodeDescription> getNodes (@NonNull InetAddress address) {
    return getNodes(address, defaultPort);
  }

  @SneakyThrows
  public List<EpmdInfo.NodeDescription> getNodes (@NonNull InetAddress address, int port) {
    log.debug("Getting nodes from {}:{}", address, port);
    try (val connection = new Connection(address, port)) {
      val info = connection.send(new GetEpmdInfo(), EpmdInfo.class);
      return info.getNodes();
    }
  }
}
