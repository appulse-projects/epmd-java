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

import static ch.qos.logback.classic.Level.DEBUG;
import static ch.qos.logback.classic.Level.INFO;
import static java.util.Optional.ofNullable;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import java.io.File;
import java.util.ResourceBundle;

import io.appulse.epmd.java.server.command.CommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;

import ch.qos.logback.classic.Logger;
import com.beust.jcommander.JCommander;
import lombok.NonNull;
import lombok.val;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
public final class CommandLineParser {

  public static CommandExecutor parse (@NonNull String[] args) {
    CommonOptions commonOptions = new CommonOptions();
    JCommander commander = buildJCommander(commonOptions);
    try {
      commander.parse(args);
    } catch (RuntimeException ex) {
      System.err.format("%nParsing arguments failed.%n%s%n%n", ex.getMessage());
      commander.usage();
      throw ex;
    }

    val command = getParsedCommand(commander, commonOptions);
    return ofNullable(commander.getCommands().get(command))
        .map(JCommander::getObjects)
        .map(it -> it.get(0))
        .filter(it -> it instanceof CommandOptions)
        .map(it -> (CommandOptions) it)
        .map(it -> it.getComandExecutor(commonOptions))
        .orElseThrow(RuntimeException::new);
  }

  private static JCommander buildJCommander (@NonNull CommonOptions commonOptions) {
    val bundle = ResourceBundle.getBundle("cli");
    val programName = "java -jar " + getProgramName();

    val builder = JCommander.newBuilder()
        .programName(programName)
        .resourceBundle(bundle)
        .addObject(commonOptions);

    CommandOptions.all()
        .forEach(builder::addCommand);

    return builder.build();
  }

  private static String getProgramName () {
    val name = new File(CommandLineParser.class.getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath())
            .getName();

    return name.contains(".jar")
           ? name
           : "epmd.jar";
  }

  private static String getParsedCommand (@NonNull JCommander commander, @NonNull CommonOptions commonOptions) {
    if (commonOptions.isHelp()) {
      System.out.println("\nSee the Erlang epmd manual page for info about the usage.\n");
      commander.usage();
      Runtime.getRuntime().exit(0);
    }

    val root = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
    if (commonOptions.isDebug()) {
      root.setLevel(DEBUG);
    } else {
      root.setLevel(INFO);
    }

    val command = commander.getParsedCommand();
    if (command == null || command.isEmpty()) {
      System.out.println("\nEPMD command doesn't set\n");
      commander.usage();
      Runtime.getRuntime().exit(1);
    }
    return command;
  }

  private CommandLineParser () {
  }
}
