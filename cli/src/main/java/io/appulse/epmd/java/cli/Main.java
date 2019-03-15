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

import picocli.CommandLine;

/**
 * Main class, program's entry point.
 *
 * @since 0.3.2
 * @author Artem Labazin
 */
public final class Main {

  /**
   * Main method.
   *
   * @param args program's arguments
   */
  public static void main (String[] args) {
    CommandLine.run(new Epmd(), args);
  }

  private Main () {
  }
}