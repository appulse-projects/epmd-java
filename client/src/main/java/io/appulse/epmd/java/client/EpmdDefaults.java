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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * EpmdDefaults values.
 */
@Slf4j
public final class EpmdDefaults {

  /**
   * Default EPMD client address (localhost).
   */
  public static final InetAddress ADDRESS = getDefaultInetAddress();

  /**
   * Default EPMD client port (4369).
   */
  public static final int PORT = getDefaultPort();

  @SneakyThrows
  private static InetAddress getDefaultInetAddress () {
    return InetAddress.getLocalHost();
  }

  private static int getDefaultPort () {
    int port = 4369;
    try {
      val value = System.getenv("ERL_EPMD_PORT");
      if (value != null) {
        port = Integer.parseInt(value);
      }
    } catch (NumberFormatException | SecurityException ex) {
      throw new IllegalStateException("Couldn't extract EPMD port", ex);
    }
    log.debug("Default EPMD port is: '{}'", port);
    return port;
  }

  private EpmdDefaults () {
    throw new UnsupportedOperationException();
  }
}
