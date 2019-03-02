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

import static lombok.AccessLevel.PRIVATE;

import java.net.InetAddress;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;

@RequiredArgsConstructor
@SuppressWarnings("checkstyle:HiddenField")
@FieldDefaults(level = PRIVATE, makeFinal = true)
class ConnectionManager {

  @Getter
  @NonNull
  InetAddress address;

  @Getter
  @NonNull
  Integer port;

  Connection connect () {
    return connect(address, port);
  }

  Connection connect (@NonNull String host) {
    return connect(host, EpmdDefaults.PORT);
  }

  @SneakyThrows
  Connection connect (@NonNull String host, int port) {
    val address = InetAddress.getByName(host);
    return connect(address, port);
  }

  Connection connect (@NonNull InetAddress address) {
    return connect(address, EpmdDefaults.PORT);
  }

  Connection connect (@NonNull InetAddress address, int port) {
    return new Connection(address, port);
  }
}
