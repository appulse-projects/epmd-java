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
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.stream.Stream;

import io.appulse.epmd.java.core.mapper.DataSerializable;
import io.appulse.epmd.java.core.mapper.Message;
import io.appulse.utils.Bytes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.val;

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
public class EpmdInfo implements DataSerializable {

  @NonNull
  Integer port;

  @NonNull
  @Singular
  List<NodeDescription> nodes;

  @Override
  public void write (@NonNull Bytes bytes) {
    bytes.put4B(port);

    if (nodes.isEmpty()) {
      bytes.put(new byte[0]);
      return;
    }

    val string = nodes.stream()
        .map(it -> String.format("name %s at port %d", it.getName(), it.getPort()))
        .collect(joining("\n"));

    bytes.put(string, ISO_8859_1);
  }

  @Override
  public void read (@NonNull Bytes bytes) {
    port = bytes.getInt();
    val string = bytes.getString(ISO_8859_1);

    nodes = Stream.of(string.split("\\n"))
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

  @Value
  @Builder
  public static class NodeDescription {

    @NonNull
    String name;

    @NonNull
    Integer port;
  }
}
