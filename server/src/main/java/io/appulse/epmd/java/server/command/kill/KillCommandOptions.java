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

package io.appulse.epmd.java.server.command.kill;

import io.appulse.epmd.java.server.command.CommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;

import com.beust.jcommander.Parameters;
import lombok.ToString;

/**
 * Kill the currently running epmd (only allowed if -names show empty database or
 * -relaxed_command_check was given when epmd was started).
 *
 * @since 0.3.2
 * @author Artem Labazin
 */
@ToString
@Parameters(commandNames = "-kill")
public class KillCommandOptions implements CommandOptions {

  @Override
  public Class<? extends CommandExecutor> getCommandExecutorClass () {
    return KillCommandExecutor.class;
  }
}
