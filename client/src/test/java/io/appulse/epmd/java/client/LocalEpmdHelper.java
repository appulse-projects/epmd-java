/*
 * Copyright 2020 the original author or authors.
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

package io.appulse.epmd.java.client;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
final class LocalEpmdHelper {

  @SneakyThrows
  static boolean exists () {
    val builder = new ProcessBuilder("which", "epmd");
    Process process;
    try {
      process = builder.start();
    } catch (IOException ex) {
      return false;
    }
    if (!process.waitFor(1, MINUTES)) {
      process.destroy();
      return false;
    }
    return process.exitValue() == 0;
  }

  static void run () {
    if (isRunning()) {
      kill();
    }

    val builder = new ProcessBuilder("epmd", "-daemon", "-relaxed_command_check");
    try {
      val process = builder.start();
      if (!process.waitFor(1, MINUTES)) {
        process.destroy();
        throw new RuntimeException("Local EPMD daemon doesn't respond");
      }
      if (process.exitValue() != 0) {
        process.destroy();
        throw new RuntimeException("Local EPMD daemon doesn't run");
      }
      SECONDS.sleep(1);
    } catch (Exception ex) {
      log.error("starting EPMD error", ex);
    }
  }

  static void kill () {
    if (!isRunning()) {
      return;
    }

    val pids = getEpmdPids();
    val builder = new ProcessBuilder("kill", "-s 9", pids);
    try {
      val process = builder.start();
      if (!process.waitFor(1, MINUTES)) {
        process.destroy();
        throw new RuntimeException("Killing local EPMD is too long, pid: " + pids);
      }
      if (process.exitValue() != 0) {
        process.destroy();
        throw new RuntimeException("Couldn't kill local EPMD with PID " + pids);
      }
    } catch (Exception ex) {
      log.error("killing EPMD error", ex);
    }
  }

  @SneakyThrows
  static boolean isRunning () {
    val builder = new ProcessBuilder("epmd", "-names");
    val process = builder.start();
    if (!process.waitFor(1, MINUTES)) {
      process.destroy();
      return false;
    }
    return process.exitValue() == 0;
  }

  @SneakyThrows
  static String getEpmdPids () {
    val builder = new ProcessBuilder("pgrep", "-f", "epmd");
    val process = builder.start();
    if (!process.waitFor(1, MINUTES)) {
      process.destroy();
      throw new RuntimeException("pgrep doesn't respond");
    }
    if (process.exitValue() != 0) {
      process.destroy();
      throw new RuntimeException("pgrep exit error");
    }

    val result = new StringBuilder();
    try (val reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        result.append(line).append(' ');
      }
    }
    return result.toString().trim();
  }

  private LocalEpmdHelper () {
    throw new UnsupportedOperationException();
  }
}
