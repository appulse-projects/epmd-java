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

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.client.EpmdClient;
import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.epmd.java.server.command.AbstractCommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;

import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * Stop command executor.
 *
 * @since 0.3.2
 * @author Artem Labazin
 */
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StopCommandExecutor extends AbstractCommandExecutor {

  StopCommandOptions options;

  /**
   * Constructor.
   *
   * @param commonOptions common command options for all commands
   *
   * @param options specific command options
   */
  public StopCommandExecutor (CommonOptions commonOptions, @NonNull CommandOptions options) {
    super(commonOptions);
    this.options = ofNullable(options)
        .filter(it -> it instanceof StopCommandOptions)
        .map(it -> (StopCommandOptions) it)
        .orElse(new StopCommandOptions());

    log.debug("Stop options: {}", options);
  }

  @Override
  public void execute () {
    try (EpmdClient client = new EpmdClient(getPort())) {
      client.stop(options.getName());
    }
    log.info("Node '{}' was stopped", options.getName());
  }
}
