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
import static java.util.stream.Collectors.toSet;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

import io.appulse.epmd.java.server.command.CommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;
import io.appulse.utils.ResourceUtils;

import ch.qos.logback.classic.Logger;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
public final class CommandLineParser {

  public static CommandExecutor parse (@NonNull String[] args) {
    val newArgs = setDefaultCommandIfNeed(args);

    CommonOptions commonOptions = new CommonOptions();
    JCommander commander = buildJCommander(commonOptions);
    try {
      commander.parse(newArgs);
    } catch (RuntimeException ex) {
      System.err.format("%nParsing arguments failed: %s%n%n", ex.getMessage());
      printUsage();
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

  private static String[] setDefaultCommandIfNeed (String[] args) {
    val commands = CommandOptions.ALL.stream()
        .map(Object::getClass)
        .map(it -> it.getAnnotation(Parameters.class))
        .map(Parameters::commandNames)
        .map(it -> it[0])
        .collect(toSet());

    if (Stream.of(args).anyMatch(commands::contains)) {
      return args;
    }

    val result = Arrays.copyOf(args, args.length + 1);
    result[args.length] = "-server";
    return result;
  }

  private static JCommander buildJCommander (@NonNull CommonOptions commonOptions) {
    val programName = "java -jar " + getProgramName();

    val builder = JCommander.newBuilder()
        .programName(programName)
        .addObject(commonOptions);

    CommandOptions.ALL.forEach(builder::addCommand);
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

  @SneakyThrows
  private static String getParsedCommand (@NonNull JCommander commander, @NonNull CommonOptions commonOptions) {
    if (commonOptions.isHelp()) {
      printUsage();
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
      printUsage();
      Runtime.getRuntime().exit(1);
    }
    return command;
  }

  private static void printUsage () {
    val usage = ResourceUtils.getResource("/usage.txt")
        .orElseThrow(RuntimeException::new);

    System.out.println(usage);
  }

  private CommandLineParser () {
  }
}
