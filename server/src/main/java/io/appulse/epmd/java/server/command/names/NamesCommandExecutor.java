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

package io.appulse.epmd.java.server.command.names;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.client.EpmdClient;
import io.appulse.epmd.java.core.model.response.EpmdInfo.NodeDescription;
import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.epmd.java.server.command.AbstractCommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;

import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NamesCommandExecutor extends AbstractCommandExecutor {

  NamesCommandOptions options;

  public NamesCommandExecutor (CommonOptions commonOptions, @NonNull CommandOptions options) {
    super(commonOptions);
    this.options = ofNullable(options)
        .filter(it -> it instanceof NamesCommandOptions)
        .map(it -> (NamesCommandOptions) it)
        .orElse(new NamesCommandOptions());
  }

  @Override
  public void execute () {
    try (val client = new EpmdClient(getPort())) {
      client.getNodes()
          .stream()
          .map(NodeDescription::getName)
          .forEach(System.out::println);
    }
  }
}