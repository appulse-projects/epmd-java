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

package io.appulse.epmd.java.server;

import io.appulse.epmd.java.server.cli.CommandLineParser;

import lombok.NonNull;

/**
 *
 * @author Artem Labazin
 * @since 0.3.0
 */
public final class Main {

  public static void main (@NonNull String[] args) {
    try {
      CommandLineParser.parse(args)
          .execute();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
  }

  private Main () {
  }
}
