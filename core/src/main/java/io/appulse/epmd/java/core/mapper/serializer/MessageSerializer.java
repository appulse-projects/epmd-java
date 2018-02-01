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

package io.appulse.epmd.java.core.mapper.serializer;

import static io.appulse.epmd.java.core.model.Tag.UNDEFINED;
import static io.appulse.utils.BytesUtils.align;
import static io.appulse.utils.BytesUtils.asBytes;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.appulse.epmd.java.core.mapper.Message;
import io.appulse.epmd.java.core.mapper.exception.MessageAnnotationMissingException;
import io.appulse.epmd.java.core.mapper.serializer.exception.NoApplicableSerializerException;

import lombok.SneakyThrows;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public final class MessageSerializer {

  private static final List<Serializer> SERIALIZERS;

  static {
    SERIALIZERS = new CopyOnWriteArrayList<>(asList(
        new DataSerializer(),
        new EnumSerializer()
    ));
  }

  @SneakyThrows
  public byte[] serialize (Object obj) {
    Class<?> type = ofNullable(obj)
        .map(Object::getClass)
        .orElseThrow(NullPointerException::new);

    val annotation = ofNullable(type)
        .map(it -> it.getAnnotation(Message.class))
        .orElseThrow(MessageAnnotationMissingException::new);

    val body = SERIALIZERS.stream()
        .filter(it -> it.isApplicable(type))
        .findAny()
        .orElseThrow(NoApplicableSerializerException::new)
        .serialize(obj, type);

    val length = annotation.value() == UNDEFINED
                 ? body.length
                 : body.length + Byte.BYTES;

    val buffer = ByteBuffer.allocate(annotation.lengthBytes() + length);
    if (annotation.lengthBytes() > 0) {
      buffer.put(align(asBytes(length), annotation.lengthBytes()));
    }
    if (annotation.value() != UNDEFINED) {
      buffer.put(annotation.value().getCode());
    }
    return buffer.put(body).array();
  }
}
