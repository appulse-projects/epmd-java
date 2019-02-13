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

package io.appulse.epmd.java.core.model.response;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.stream.Stream;

import io.appulse.epmd.java.core.mapper.DataSerializable;
import io.appulse.epmd.java.core.model.response.EpmdDump.NodeDump.Status;
import io.appulse.utils.Bytes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.val;

/**
 * EPMD node infos dump response.
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class EpmdDump implements DataSerializable {

  @NonNull
  Integer port;

  @NonNull
  @Singular
  List<NodeDump> nodes;

  @Override
  public void write (@NonNull Bytes bytes) {
    bytes.put4B(port);

    if (nodes.isEmpty()) {
      bytes.put(new byte[0]);
      return;
    }

    val string = nodes.stream()
          .map(it -> String.format(
              "%s name\t<%s> at port %d, fd = %d",
              it.getStatus(), it.getName(), it.getPort(), it.getFileDescriptor())
          )
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
          .map(it -> NodeDump.builder()
              .status(Status.of(it[0]))
              .name(it[2].substring(1, it[2].length() - 1))
              .port(Integer.parseInt(it[5].substring(0, it[5].length() - 1)))
              .fileDescriptor(Integer.parseInt(it[8]))
              .build()
          )
          .collect(toList());
  }

  /**
   * Node dump information.
   */
  public static final class NodeDump {

    private final Status status;

    private final String name;

    private final int port;

    private final int fileDescriptor;

    @Builder
    NodeDump (Status status, String name, int port, int fileDescriptor) {
      this.status = status;
      this.name = name;
      this.port = port;
      this.fileDescriptor = fileDescriptor;
    }

    /**
     * Returns node status.
     *
     * @return {@link Status} instance.
     */
    public Status getStatus () {
      return status;
    }

    /**
     * Returns node's name.
     *
     * @return node's name.
     */
    public String getName () {
      return name;
    }

    /**
     * Returns node's port.
     *
     * @return port.
     */
    public int getPort () {
      return port;
    }

    /**
     * Returns node's file descriptor.
     *
     * @return file descriptor.
     */
    public int getFileDescriptor () {
      return fileDescriptor;
    }

    @Override
    public String toString () {
      return "NodeDump{" + "status=" + status + ", name=" + name + ", port=" + port + ", fileDescriptor="
             + fileDescriptor + '}';
    }

    /**
     * Node's status enum.
     */
    public enum Status {

      /**
       * Active node.
       */
      ACTIVE("active"),

      /**
       * Inactive node.
       */
      OLD_OR_UNUSED("old/unused"),

      /**
       * Unknown node status.
       */
      UNKNOWN("");

      private final String text;

      Status (String text) {
        this.text = text;
      }

      @Override
      public String toString () {
        return text;
      }

      /**
       * Parses status string to {@link Status} instance.
       *
       * @param str status string representation.
       *
       * @return parsed {@link Status} instance.
       */
      static Status of (String str) {
        return Stream.of(values())
            .filter(it -> it.text.equalsIgnoreCase(str))
            .findAny()
            .orElse(UNKNOWN);
      }
    }
  }
}
