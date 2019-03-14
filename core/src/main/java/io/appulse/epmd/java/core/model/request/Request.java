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

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.TaggedMessage;
import io.appulse.utils.Bytes;

import lombok.NonNull;
import lombok.val;

/**
 * Marker interface for request objects, which should be serialized.
 *
 * @since 0.4.0
 * @author Artem Labazin
 */
public interface Request extends TaggedMessage {

  /**
   * Parses request message from a byte array into an object.
   *
   * @param array the byte array
   *
   * @param <T> parsed type, extends {@link Request}
   *
   * @return the parsed object
   */
  static <T extends Request> T parse (@NonNull byte[] array) {
    val bytes = Bytes.wrap(array);
    return parse(bytes);
  }

  /**
   * Parses request message from a byte array into an object.
   *
   * @param bytes the byte array
   *
   * @param <T> parsed type, extends {@link Request}
   *
   * @return the parsed object
   */
  static <T extends Request> T parse (@NonNull Bytes bytes) {
    if (bytes.readableBytes() < Short.BYTES) {
      throw new IllegalArgumentException("Not enought bytes");
    }
    val messageLength = bytes.readUnsignedShort();
    return parse(bytes, messageLength);
  }

  /**
   * Parses request message from a byte array into an object.
   *
   * @param array the byte array
   *
   * @param length the readable length of th byte array
   *
   * @param <T> parsed type, extends {@link Request}
   *
   * @return the parsed object
   */
  static <T extends Request> T parse (@NonNull byte[] array, int length) {
    val bytes = Bytes.wrap(array);
    return parse(bytes, length);
  }

  /**
   * Parses request message from a byte array into an object.
   *
   * @param bytes the byte array
   *
   * @param length the readable length of th byte array
   *
   * @param <T> parsed type, extends {@link Request}
   *
   * @return the parsed object
   */
  @SuppressWarnings("unchecked")
  static <T extends Request> T parse (@NonNull Bytes bytes, int length) {
    if (bytes.readableBytes() < length) {
      throw new IllegalArgumentException("Doesn't have enought bytes");
    }

    switch (Tag.of(bytes.readByte())) {
    case ALIVE2_REQUEST:
      return (T) new Registration(bytes);
    case PORT_PLEASE2_REQUEST:
      return (T) new GetNodeInfo(bytes);
    case NAMES_REQUEST:
      return (T) new GetEpmdInfo();
    case DUMP_REQUEST:
      return (T) new GetEpmdDump();
    case KILL_REQUEST:
      return (T) new Kill();
    case STOP_REQUEST:
      return (T) new Stop(bytes);
    default:
      throw new IllegalArgumentException();
    }
  }

  /**
   * Converts the object into a byte array.
   *
   * @return the byte array
   */
  byte[] toBytes ();
}
