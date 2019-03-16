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

package io.appulse.epmd.java.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Command(
    name = "epmd",
    description = "Erlang Port Mapper Daemon",
    mixinStandardHelpOptions = true,
    version = "epmd 2.0.0",
    subcommands = {
      SubcommandNames.class,
      SubcommandServer.class,
      SubcommandStop.class,
      SubcommandKill.class
    }
)
class Epmd implements Runnable {

  @Option(
      names = { "-p", "--port" },
      paramLabel = "PORT",
      description =
        "Let epmd listen to another port than default ${DEFAULT-VALUE}. " +
        "This can also be set using environment variable ERL_EPMD_PORT"
  )
  @Builder.Default
  int port = 4369;

  @Option(
      names = { "-d", "--debug" },
      description = "Enables debugging logs"
  )
  boolean debug;

  @Override
  public void run () {
    log.error("You must specify one of the commands");
  }
}
