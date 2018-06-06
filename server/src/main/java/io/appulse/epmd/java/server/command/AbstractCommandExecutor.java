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

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import io.appulse.epmd.java.server.cli.CommonOptions;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 * Abstract {@link CommandExecutor} implementation, which incapsulates common fields.
 *
 * @since 0.3.2
 * @author Artem Labazin
 */
@Getter(PROTECTED)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public abstract class AbstractCommandExecutor implements CommandExecutor {

  boolean debug;

  int port;

  /**
   * Constructor for incapsulating common command options.
   *
   * @param commonOptions common command options
   */
  protected AbstractCommandExecutor (@NonNull CommonOptions commonOptions) {
    debug = commonOptions.isDebug();
    port = commonOptions.getPort();
  }
}

