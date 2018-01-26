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
import io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
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
public class EpmdDump {

  @Field(bytes = 4)
  int port;

  @Singular
  @Field(serializer = NodeDumpFieldSerializer.class, deserializer = NodeDumpFieldDeserializer.class)
  List<NodeDump> nodes;

  public static final class NodeDump {

    private final Status status;

    private final String name;

    private final int port;

    private final int fileDescriptor;

    public NodeDump (Status status, String name, int port, int fileDescriptor) {
      this.status = status;
      this.name = name;
      this.port = port;
      this.fileDescriptor = fileDescriptor;
    }

    public Status getStatus () {
      return status;
    }

    public String getName () {
      return name;
    }

    public int getPort () {
      return port;
    }

    public int getFileDescriptor () {
      return fileDescriptor;
    }

    @Override
    public String toString () {
      return "NodeDump{" + "status=" + status + ", name=" + name + ", port=" + port + ", fileDescriptor="
             + fileDescriptor + '}';
    }

    public enum Status {

      ACTIVE("active"),
      OLD_OR_UNUSED("old/unused"),
      UNDEFINED("");

      private final String text;

      Status (String text) {
        this.text = text;
      }

      @Override
      public String toString () {
        return text;
      }

      static Status of (String str) {
        return Stream.of(values())
            .filter(it -> it.text.equalsIgnoreCase(str))
            .findAny()
            .orElse(UNDEFINED);
      }
    }
  }

  public static final class NodeDumpFieldSerializer implements FieldSerializer<List<NodeDump>> {

    @Override
    public byte[] write (List<NodeDump> value) {
      if (value == null || value.isEmpty()) {
        return new byte[0];
      }
      return value.stream()
          .map(it -> String.format(
              "%s name\t<%s> at port %d, fd = %d",
              it.getStatus(), it.getName(), it.getPort(), it.getFileDescriptor())
          )
          .collect(joining("\n"))
          .getBytes(ISO_8859_1);
    }
  }

  public static final class NodeDumpFieldDeserializer implements FieldDeserializer<List<NodeDump>> {

    @Override
    public List<NodeDump> read (byte[] bytes, FieldDescriptor descriptor) {
      if (bytes == null || bytes.length == 0) {
        return emptyList();
      }
      // log.error("STR: {}", new String(bytes, ISO_8859_1));
      return Stream.of(new String(bytes, ISO_8859_1).split("\\n"))
          .map(String::trim)
          .filter(it -> !it.isEmpty())
          .map(it -> Stream.of(it.split("\\s+"))
              .map(String::trim)
              .filter(token -> !token.isEmpty())
              .toArray(String[]::new)
          )
          .map(it -> new NodeDump(
              Status.of(it[0]),
              it[2].substring(1, it[2].length() - 1),
              Integer.parseInt(it[5].substring(0, it[5].length() - 1)),
              Integer.parseInt(it[8])
          ))
          //                    .map(it -> NodeDump.builder()
          //                            .status(Status.of(it[0]))
          //                            .name(it[2].substring(1, it[2].length() - 1))
          //                            .port(Integer.parseInt(it[5].substring(0, it[5].length() - 1)))
          //                            .fileDescriptor(Integer.parseInt(it[8]))
          //                            .build()
          //                    )
          .collect(toList());
    }
  }
}
