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

import static io.appulse.epmd.java.core.model.Tag.PORT_PLEASE2_REQUEST;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.core.mapper.ExpectedResponse;
import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.response.NodeInfo;
import io.appulse.utils.Bytes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 * Get the distribution port of another Node.
 * <p>
 * When one node wants to connect to another node it starts with a this request to the EPMD
 * on the host where the node resides to get the distribution port that the node listens to.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@ExpectedResponse(NodeInfo.class)
public class GetNodeInfo implements Request {

  @NonNull
  String name;

  @Override
  public void write (@NonNull Bytes bytes) {
    bytes.put(name, ISO_8859_1);
  }

  @Override
  public void read (@NonNull Bytes bytes) {
    name = bytes.getString(ISO_8859_1);
  }

  @Override
  public Tag getTag () {
    return PORT_PLEASE2_REQUEST;
  }
}
