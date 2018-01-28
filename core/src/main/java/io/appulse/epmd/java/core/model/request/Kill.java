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

package io.appulse.epmd.java.core.model.request;

import static io.appulse.epmd.java.core.model.Tag.KILL_REQUEST;

import io.appulse.epmd.java.core.mapper.ExpectedResponse;
import io.appulse.epmd.java.core.mapper.Message;
import io.appulse.epmd.java.core.mapper.DataSerializable;
import io.appulse.epmd.java.core.model.response.KillResult;
import io.appulse.utils.Bytes;

import lombok.ToString;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@ToString
@Message(KILL_REQUEST)
@ExpectedResponse(KillResult.class)
public class Kill implements DataSerializable {

  @Override
  public void write (Bytes bytes) {
    // nothing
  }

  @Override
  public void read (Bytes bytes) {
    // nothing
  }
}
