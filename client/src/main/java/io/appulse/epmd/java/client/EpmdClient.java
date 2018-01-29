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

import static io.appulse.epmd.java.core.model.response.KillResult.OK;
import static lombok.AccessLevel.PRIVATE;

import java.io.Closeable;
import java.net.InetAddress;
import java.util.List;

import io.appulse.epmd.java.client.exception.EpmdRegistrationException;
import io.appulse.epmd.java.core.model.NodeType;
import io.appulse.epmd.java.core.model.Protocol;
import io.appulse.epmd.java.core.model.Version;
import io.appulse.epmd.java.core.model.request.GetEpmdDump;
import io.appulse.epmd.java.core.model.request.Kill;
import io.appulse.epmd.java.core.model.request.Registration;
import io.appulse.epmd.java.core.model.response.EpmdDump;
import io.appulse.epmd.java.core.model.response.KillResult;
import io.appulse.epmd.java.core.model.response.RegistrationResult;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * EPMD client.
 *
 * @author Artem Labazin
 * @since 0.2.2
 */
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class EpmdClient implements Closeable {

  @Getter(lazy = true, value = PRIVATE)
  Connection localConnection = connect();

  @Delegate
  LookupService lookupService;

  @Delegate
  NodesLocatorService nodesLocatorService;

  InetAddress address;

  Integer port;

  public EpmdClient () {
    this(Default.ADDRESS, Default.PORT);
  }

  public EpmdClient (InetAddress address) {
    this(address, Default.PORT);
  }

  public EpmdClient (int port) {
    this(Default.ADDRESS, port);
  }

  @Builder
  public EpmdClient (@NonNull InetAddress address, int port) {
    lookupService = new LookupService(address, port);
    nodesLocatorService = new NodesLocatorService(address, port);
    this.address = address;
    this.port = port;
  }

  public int register (String name, int nodePort, NodeType type, Protocol protocol, Version low, Version high) {
    val request = Registration.builder()
        .name(name)
        .port(nodePort)
        .type(type)
        .protocol(protocol)
        .high(high)
        .low(low)
        .build();

    return register(request);
  }

  public int register (@NonNull Registration request) {
    log.debug("Registering: {}", request.getName());
    val connection = getLocalConnection();

    RegistrationResult response;
    try {
      response = connection.send(request, RegistrationResult.class);
    } catch (Exception ex) {
      log.error("{} wasn't registered successfully", request.getName());
      throw new EpmdRegistrationException(ex);
    }

    if (!response.isOk()) {
      log.error("{} wasn't registered successfully", request.getName());
      throw new EpmdRegistrationException();
    }

    log.info("{} was registered successfully", request.getName());
    return response.getCreation();
  }

  public List<EpmdDump.NodeDump> dumpAll () {
    try (val connection = new Connection(address, port)) {
      val dump = connection.send(new GetEpmdDump(), EpmdDump.class);
      return dump.getNodes();
    }
  }

  /**
   * This request kills the running EPMD server.
   *
   * @return was it killed or not.
   */
  public boolean kill () {
    try (val connection = new Connection(address, port)) {
      val result = connection.send(new Kill(), KillResult.class);
      return result == OK;
    } catch (RuntimeException ex) {
      return false;
    } finally {
      close();
    }
  }

  public boolean isClosed () {
    val connection = getLocalConnection();
    return connection.isClosed();
  }

  @Override
  public void close () {
    val connection = getLocalConnection();
    if (connection.isClosed()) {
      log.debug("EPMD client was already closed");
    } else {
      connection.close();
      log.debug("EPMD client was closed");
    }
  }

  public void clearCaches () {
    lookupService.clearCache();
  }

  @Override
  protected void finalize () throws Throwable {
    close();
    super.finalize();
  }

  private Connection connect () {
    return new Connection(address, port);
  }

  private static class Default {

    private static final InetAddress ADDRESS = getDefaultInetAddress();

    private static final int PORT = getDefaultPort();

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
        throw new RuntimeException(ex);
      }
      log.debug("Default EPMD port is: {}", port);
      return port;
    }
  }
}
