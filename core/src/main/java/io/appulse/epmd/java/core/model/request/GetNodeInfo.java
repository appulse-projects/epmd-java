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
import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.core.mapper.ExpectedResponse;
import io.appulse.epmd.java.core.mapper.Field;
import io.appulse.epmd.java.core.mapper.Message;
import io.appulse.epmd.java.core.model.response.NodeInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Message(PORT_PLEASE2_REQUEST)
@FieldDefaults(level = PRIVATE)
@ExpectedResponse(NodeInfo.class)
public class GetNodeInfo {

  @Field
  @NonNull
  String name;
}
