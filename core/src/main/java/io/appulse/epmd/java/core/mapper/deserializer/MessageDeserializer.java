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

package io.appulse.epmd.java.core.mapper.deserializer;

import static io.appulse.epmd.java.core.model.Tag.UNDEFINED;
import static io.appulse.utils.BytesUtil.asInteger;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.util.List;

import io.appulse.epmd.java.core.mapper.Message;
import io.appulse.epmd.java.core.mapper.deserializer.exception.InvalidReceivedMessageLengthException;
import io.appulse.epmd.java.core.mapper.deserializer.exception.InvalidReceivedMessageTagException;
import io.appulse.epmd.java.core.mapper.deserializer.exception.NoApplicableDeserializerException;
import io.appulse.epmd.java.core.mapper.exception.MessageAnnotationMissingException;
import io.appulse.epmd.java.core.model.Tag;
import io.appulse.utils.Bytes;

import lombok.SneakyThrows;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public final class MessageDeserializer {

  private static final List<Deserializer> DESERIALIZERS;

  static {
    DESERIALIZERS = asList(
        new DataDeserializer(),
        new EnumDeserializer()
    );
  }

  @SneakyThrows
  public <T> T deserialize (byte[] bytes, Class<T> type) {
    val buffer = ofNullable(bytes)
        .map(Bytes::wrap)
        .orElseThrow(NullPointerException::new);

    val annotation = ofNullable(type)
        .map(it -> it.getAnnotation(Message.class))
        .orElseThrow(MessageAnnotationMissingException::new);

    if (annotation.lengthBytes() > 0) {
      val receivedMessageLength = asInteger(buffer.getBytes(annotation.lengthBytes()));
      if (receivedMessageLength != buffer.remaining()) {
        val message = String.format("Expected length is %d - %d bytes, but actual length is %d bytes.",
                                    annotation.lengthBytes(), receivedMessageLength, buffer.remaining());
        throw new InvalidReceivedMessageLengthException(message);
      }
    }

    if (annotation.value() != UNDEFINED) {
      val tag = Tag.of(buffer.getByte());
      if (annotation.value() != tag) {
        val message = String.format("Expected tag is: %s, but actual tag is: %s",
                                    annotation.value(), tag);
        throw new InvalidReceivedMessageTagException(message);
      }
    }

    return DESERIALIZERS.stream()
        .filter(it -> it.isApplicable(type))
        .findAny()
        .orElseThrow(NoApplicableDeserializerException::new)
        .deserialize(buffer, type);
  }
}
