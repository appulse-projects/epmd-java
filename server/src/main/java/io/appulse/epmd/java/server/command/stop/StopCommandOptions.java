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

package io.appulse.epmd.java.server.command.stop;

import io.appulse.epmd.java.server.command.CommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Artem Labazin
 * @since 0.3.1
 */
@Getter
@ToString
@Parameters(commandNames = "-stop")
public class StopCommandOptions implements CommandOptions {

  @Parameter
  String name;

  @Override
  public Class<? extends CommandExecutor> getCommandExecutorClass () {
    return StopCommandExecutor.class;
  }
}
