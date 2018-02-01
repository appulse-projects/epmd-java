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

package io.appulse.epmd.java.server.command.server.handler;

import static io.appulse.epmd.java.core.model.Tag.KILL_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static io.appulse.epmd.java.core.model.response.KillResult.OK;

import java.io.ByteArrayOutputStream;

import io.appulse.epmd.java.server.command.server.RequestTestUtil;
import io.appulse.epmd.java.server.command.server.ServerCommandOptions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import lombok.val;

public class KillRequestHandlerTest {

  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  RequestHandler handler = new KillRequestHandler();

  @Test
  public void handleOk () {
    exit.expectSystemExitWithStatus(1);

    val output = new ByteArrayOutputStream();
    val options = new ServerCommandOptions();
    options.setChecks(true);
    val request = RequestTestUtil.createRequest(options, output);

    handler.handle(request);

    assertThat(output.toByteArray())
        .isEqualTo(RequestTestUtil.serialize(OK));
  }

  @Test
  public void handleNok () {

  }

  @Test
  public void getTag () {
    assertThat(handler.getTag())
        .isEqualTo(KILL_REQUEST);
  }
}
