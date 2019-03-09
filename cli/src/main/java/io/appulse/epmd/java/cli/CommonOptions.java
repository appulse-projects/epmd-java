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

import io.appulse.epmd.java.cli.command.KillOptions;
import io.appulse.epmd.java.cli.command.NamesOptions;
import io.appulse.epmd.java.cli.command.ServerCommand;
import io.appulse.epmd.java.cli.command.StopCommand;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Slf4j
@Command(subcommands = {
  KillOptions.class,
  NamesOptions.class,
  ServerCommand.class,
  StopCommand.class
})
public class CommonOptions implements Runnable {

  @Option(
    names = { "-v", "--version" },
    versionHelp = true
  )
  boolean versionRequested;

  @Option(
    names = { "-h", "--help" },
    usageHelp = true
  )
  boolean helpRequested;

  @Option(
    names = { "-p", "--port" }
  )
  int port = 4369;

  @Option(
    names = { "-d", "--debug" }
  )
  boolean debugInfoRequested;

  @Override
  public void run () {
    log.info("\nversion={}\nusage={}\nport={}\ndebug={}",
             versionRequested, helpRequested,
             port, debugInfoRequested);
  }
}
