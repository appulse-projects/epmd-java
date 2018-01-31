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

package io.appulse.epmd.java.server.cli;

import static lombok.AccessLevel.PRIVATE;

import com.beust.jcommander.Parameter;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Common parsed program's options holder.
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
@Data
@FieldDefaults(level = PRIVATE)
public class CommonOptions {

  @Parameter(
      names = { "--help", "-h" },
      help = true,
      hidden = true
  )
  boolean help;

  @Parameter(
      names = "-port",
      arity = 1,
      descriptionKey = "common.port"
  )
  int port = 4369;

  @Parameter(
      names = { "-d", "-debug" },
      descriptionKey = "common.debug"
  )
  boolean debug = false;
}
