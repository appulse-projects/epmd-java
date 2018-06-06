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

package io.appulse.epmd.java.server.command;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.epmd.java.server.command.kill.KillCommandOptions;
import io.appulse.epmd.java.server.command.names.NamesCommandOptions;
import io.appulse.epmd.java.server.command.server.ServerCommandOptions;
import io.appulse.epmd.java.server.command.stop.StopCommandOptions;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

/**
 * Marker interface for command options.
 *
 * @since 0.3.2
 * @author Artem Labazin
 */
public interface CommandOptions {

  /**
   * All known command options.
   */
  List<CommandOptions> ALL = unmodifiableList(asList(
      new KillCommandOptions(),
      new NamesCommandOptions(),
      new ServerCommandOptions(),
      new StopCommandOptions()
  ));

  /**
   * Returns {@link CommandExecutor} which is associated with this options.
   *
   * @return {@link CommandExecutor} type for instantiating
   */
  Class<? extends CommandExecutor> getCommandExecutorClass ();

  /**
   * Instantiate new {@link CommandExecutor} with specified {@link CommonOptions}.
   *
   * @param commonOptions common options part
   *
   * @return new instance of {@link CommandExecutor}
   */
  @SneakyThrows
  default CommandExecutor createComandExecutor (@NonNull CommonOptions commonOptions) {
    val type = getCommandExecutorClass();
    val constructor = type.getConstructor(CommonOptions.class, CommandOptions.class);
    return constructor.newInstance(commonOptions, this);
  }
}
