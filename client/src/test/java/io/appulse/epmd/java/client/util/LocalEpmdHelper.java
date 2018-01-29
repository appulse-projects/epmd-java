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

package io.appulse.epmd.java.client.util;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lombok.SneakyThrows;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.2.1
 */
public final class LocalEpmdHelper {

  @SneakyThrows
  public static boolean exists () {
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

  @SneakyThrows
  public static void run () {
    if (isRunning()) {
      kill();
    }

    val builder = new ProcessBuilder("epmd", "-daemon");
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
  }

  @SneakyThrows
  public static void kill () {
    if (!isRunning()) {
      return;
    }
    val pid = getEpmdPid();
    val builder = new ProcessBuilder("kill", "-9", Integer.toString(pid));
    val process = builder.start();
    if (!process.waitFor(1, MINUTES)) {
      process.destroy();
      throw new RuntimeException("Killing local EPMD is too long, pid: " + pid);
    }
    if (process.exitValue() != 0) {
      process.destroy();
      throw new RuntimeException("Couldn't kill local EPMD with PID " + pid);
    }
  }

  @SneakyThrows
  public static boolean isRunning () {
    val builder = new ProcessBuilder("pgrep", "-f", "epmd");
    val process = builder.start();
    if (!process.waitFor(1, MINUTES)) {
      process.destroy();
      return false;
    }
    return process.exitValue() == 0;
  }

  @SneakyThrows
  private static int getEpmdPid () {
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
        result.append(line).append('\n');
      }
    }
    return Integer.parseInt(result.toString().trim());
  }

  private LocalEpmdHelper () {
  }
}
