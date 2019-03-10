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

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import io.appulse.epmd.java.client.EpmdClient;
import io.appulse.epmd.java.client.exception.EpmdConnectionException;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Slf4j
@Command(name = "-kill")
class CommandKillEpmdServer implements Runnable {

  @ParentCommand
  CommonOptions options;

  @Override
  public void run () {
    try (val client = new EpmdClient(options.getPort())) {
      if (client.kill().get(2, SECONDS)) {
        System.out.println("Killed");
      } else {
        System.err.println("Killing not allowed - living nodes in database.");
        System.exit(1);
      }
    } catch (CompletionException | ExecutionException ex) {
      val exception = ex.getCause();
      if (exception instanceof EpmdConnectionException) {
        System.err.println("epmd: Cannot connect to local epmd");
        System.exit(1);
      } else {
        log.error("{}", exception.getMessage(), exception);
      }
    } catch (TimeoutException ex) {
      log.error("EPMD server doesn't respond too long");
    } catch (InterruptedException ex) {
      log.error("Interrupted", ex);
    }
  }
}
