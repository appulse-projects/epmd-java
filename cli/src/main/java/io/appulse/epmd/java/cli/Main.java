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

import static java.util.Locale.ENGLISH;

import io.appulse.utils.ResourceUtils;

import lombok.val;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.RunLast;

/**
 * Main class, program's entry point.
 *
 * @since 0.3.2
 * @author Artem Labazin
 */
public final class Main {

  private static final String USAGE = ResourceUtils.getTextContent("/usage.txt").get();

  /**
   * Main method.
   *
   * @param args program's arguments
   */
  public static void main (String[] args) {
    val command = new CommandStartEpmdServer();
    val cmd = new CommandLine(command);
    try {
      val parsedResult = cmd.parseArgs(args);
      if (cmd.isUsageHelpRequested()) {
        System.out.println(USAGE);
        return;
      }
      new RunLast().handleParseResult(parsedResult);
    } catch (ParameterException ex) {
      System.err.format(ENGLISH, "%s%n%n%s", ex.getMessage(), USAGE);
      System.exit(1);
    }
  }

  private Main () {
  }
}
