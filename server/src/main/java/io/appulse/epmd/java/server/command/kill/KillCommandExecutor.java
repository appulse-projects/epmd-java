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

package io.appulse.epmd.java.server.command.kill;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.client.EpmdClient;
import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.epmd.java.server.command.AbstractCommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;

import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class KillCommandExecutor extends AbstractCommandExecutor {

  KillCommandOptions options;

  public KillCommandExecutor (CommonOptions commonOptions, @NonNull CommandOptions options) {
    super(commonOptions);
    this.options = ofNullable(options)
        .filter(it -> it instanceof KillCommandOptions)
        .map(it -> (KillCommandOptions) it)
        .orElse(new KillCommandOptions());
  }

  @Override
  public void execute () {
    boolean killed = false;
    try (EpmdClient client = new EpmdClient(getPort())) {
      killed = client.kill();
    }

    if (killed) {
      Runtime.getRuntime().exit(1);
    }
  }
}