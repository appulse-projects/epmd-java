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

package io.appulse.epmd.java.server.command.server;

import static java.util.Collections.singleton;
import static lombok.AccessLevel.PRIVATE;

import java.net.InetAddress;
import java.util.Set;

import io.appulse.epmd.java.server.command.CommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
@Data
@Parameters(
    commandNames = "server",
    commandDescriptionKey = "server.description"
)
@FieldDefaults(level = PRIVATE)
public class ServerCommandOptions implements CommandOptions {

  @Parameter(
      names = "-packet_timeout",
      descriptionKey = "server.packet_timeout"
  )
  int packetTimeout = 60;

  @Parameter(
      names = "-delay_accept",
      descriptionKey = "server.delay_accept"
  )
  int delayAccept = 0;

  @Parameter(
      names = "-delay_write",
      descriptionKey = "server.delay_write"
  )
  int delayWrite = 0;

  @Parameter(
      names = "-address",
      descriptionKey = "server.address"
  )
  Set<InetAddress> addresses = getDefaultAddresses();

  @Parameter(
      names = "-daemon",
      descriptionKey = "server.daemon"
  )
  boolean daemon = false;

  @Parameter(
      names = "-relaxed_command_check",
      descriptionKey = "server.relaxed_command_check"
  )
  boolean checks = false;

  @SneakyThrows
  private static Set<InetAddress> getDefaultAddresses () {
    return singleton(InetAddress.getByName("0.0.0.0"));
  }

  @Override
  public Class<? extends CommandExecutor> getCommandExecutorClass () {
    return ServerCommandExecutor.class;
  }
}
