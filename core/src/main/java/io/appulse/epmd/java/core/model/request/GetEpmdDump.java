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

package io.appulse.epmd.java.core.model.request;

import static io.appulse.epmd.java.core.model.Tag.DUMP_REQUEST;

import io.appulse.epmd.java.core.mapper.ExpectedResponse;
import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.response.EpmdDump;
import io.appulse.utils.Bytes;

import lombok.ToString;

/**
 * Dump all data from EPMD request.
 * <p>
 * This request is not really used, it is to be regarded as a debug feature.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@ToString
@ExpectedResponse(EpmdDump.class)
public class GetEpmdDump implements Request {

  @Override
  public void write (Bytes bytes) {
    // nothing
  }

  @Override
  public void read (Bytes bytes) {
    // nothing
  }

  @Override
  public Tag getTag () {
    return DUMP_REQUEST;
  }
}
