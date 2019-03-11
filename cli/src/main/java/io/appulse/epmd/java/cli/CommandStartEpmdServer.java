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

package io.appulse.epmd.java.cli;

import static java.util.Collections.singleton;
import static lombok.AccessLevel.PRIVATE;

import java.net.InetAddress;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Slf4j
@Getter
@ToString
@EqualsAndHashCode
@Command(
  subcommands = {
    SubcommandKillEpmdServer.class,
    SubcommandGetAllNames.class,
    SubcommandStopNode.class
  }
)
@FieldDefaults(level = PRIVATE)
class CommandStartEpmdServer implements Runnable {

  @SneakyThrows
  private static Set<InetAddress> getDefaultAddresses () {
    return singleton(InetAddress.getByName("0.0.0.0"));
  }

  @Option(
    names = { "-h", "--help" },
    usageHelp = true,
    hidden = true
  )
  boolean helpRequested;

  @Option(names = "-address")
  Set<InetAddress> addresses = getDefaultAddresses();

  @Option(names = "-port")
  int port = 4369;

  @Option(names = { "-d", "-debug" })
  boolean[] debugLevel = new boolean[0];

  @Option(names = "-daemon")
  boolean daemon;

  @Option(names = "-relaxed_command_check")
  boolean checks;

  @Option(names = "-packet_timeout")
  int packetTimeout = 60;

  @Option(names = "-delay_accept")
  int delayAccept;

  @Option(names = "-delay_write")
  int delayWrite;

  @Override
  public void run () {
    log.info("SERVER\npacketTimeout={}\ndelayAccept={}\ndelayWrite={}\naddresses={}\ndaemon={}\nchecks={}",
             packetTimeout, delayAccept, delayWrite, addresses, daemon, checks);
  }
}
