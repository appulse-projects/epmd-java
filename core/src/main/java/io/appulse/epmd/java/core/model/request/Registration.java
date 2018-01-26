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

import static io.appulse.epmd.java.core.model.Tag.ALIVE2_REQUEST;
import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.core.mapper.ExpectedResponse;
import io.appulse.epmd.java.core.mapper.Field;
import io.appulse.epmd.java.core.mapper.LengthBefore;
import io.appulse.epmd.java.core.mapper.Message;
import io.appulse.epmd.java.core.model.NodeType;
import io.appulse.epmd.java.core.model.Protocol;
import io.appulse.epmd.java.core.model.Version;
import io.appulse.epmd.java.core.model.response.RegistrationResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Message(ALIVE2_REQUEST)
@FieldDefaults(level = PRIVATE)
@ExpectedResponse(RegistrationResult.class)
public class Registration {

  @Field(bytes = 2)
  int port;

  @NonNull
  @Field(bytes = 1)
  NodeType type;

  @NonNull
  @Field(bytes = 1)
  Protocol protocol;

  @NonNull
  @Field(bytes = 2)
  Version high;

  @NonNull
  @Field(bytes = 2)
  Version low;

  @LengthBefore(bytes = 2)
  @NonNull
  @Field
  String name;

  @Field(bytes = 2)
  int extra;
}
