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

package io.appulse.epmd.java.core.model.response;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.stream.Stream;

import io.appulse.epmd.java.core.mapper.Field;
import io.appulse.epmd.java.core.mapper.FieldDescriptor;
import io.appulse.epmd.java.core.mapper.Message;
import io.appulse.epmd.java.core.mapper.deserializer.field.FieldDeserializer;
import io.appulse.epmd.java.core.mapper.serializer.field.FieldSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@Getter
@Builder
@Message(lengthBytes = 0)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class EpmdInfo {

  @Field(bytes = 4)
  int port;

  @Singular
  @Field(serializer = NodeDescriptionFieldSerializer.class, deserializer = NodeDescriptionFieldDeserializer.class)
  List<NodeDescription> nodes;

  @Value
  @Builder
  @ToString
  public static class NodeDescription {

    String name;

    int port;
  }

  public static final class NodeDescriptionFieldSerializer implements FieldSerializer<List<NodeDescription>> {

    @Override
    public byte[] write (List<NodeDescription> value) {
      if (value == null || value.isEmpty()) {
        return new byte[0];
      }
      return value.stream()
          .map(it -> String.format("name %s at port %d", it.getName(), it.getPort()))
          .collect(joining("\n"))
          .getBytes(ISO_8859_1);
    }
  }

  public static final class NodeDescriptionFieldDeserializer implements FieldDeserializer<List<NodeDescription>> {

    @Override
    public List<NodeDescription> read (byte[] bytes, FieldDescriptor descriptor) {
      if (bytes == null || bytes.length == 0) {
        return emptyList();
      }
      return Stream.of(new String(bytes, ISO_8859_1).split("\\n"))
          .map(String::trim)
          .filter(it -> !it.isEmpty())
          .map(it -> Stream.of(it.split("\\s+"))
              .map(String::trim)
              .filter(token -> !token.isEmpty())
              .toArray(String[]::new)
          )
          .map(it -> NodeDescription.builder()
              .name(it[1])
              .port(Integer.parseInt(it[4]))
              .build()
          )
          .collect(toList());
    }
  }
}
