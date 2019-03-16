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

package io.appulse.epmd.java.server;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import io.appulse.epmd.java.client.EpmdClient;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Slf4j
@Command(name = "stop")
class SubcommandStop implements Runnable {

  @ParentCommand
  Epmd options;

  @Parameters
  String name;

  @Override
  public void run () {
    try (val client = new EpmdClient(options.port)) {
      if (client.stop(name).get(2, SECONDS)) {
        log.info("the node '{}' was stopped", name);
      } else {
        log.error("couldn't stop the node '{}'", name);
        Runtime.getRuntime().exit(1);
      }
    } catch (CompletionException | ExecutionException ex) {
      val cause = ex.getCause();
      if (options.debug) {
        log.error("{}", cause.getMessage(), cause);
      } else {
        log.error("{}", cause.getMessage());
      }
      Runtime.getRuntime().exit(1);
    } catch (TimeoutException ex) {
      log.error("EPMD server doesn't respond too long");
    } catch (InterruptedException ex) {
      log.error("Interrupted", ex);
    }
  }
}
