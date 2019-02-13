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

package io.appulse.epmd.java.core.mapper.serializer;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.appulse.epmd.java.core.mapper.serializer.exception.NoApplicableSerializerException;

import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * Class for serializing different objects to byte arrays.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
public final class MessageSerializer {

  private static final List<Serializer> SERIALIZERS;

  static {
    SERIALIZERS = new CopyOnWriteArrayList<>(asList(
        new RequestSerializer(),
        new DataSerializer(),
        new EnumSerializer()
    ));
  }

  /**
   * Serializes objects to byte arrays.
   *
   * @param obj object for serialization
   *
   * @return object's byte array representation
   */
  @SneakyThrows
  public byte[] serialize (@NonNull Object obj) {
    Class<?> type = obj.getClass();
    return SERIALIZERS.stream()
        .filter(it -> it.isApplicable(type))
        .findAny()
        .orElseThrow(NoApplicableSerializerException::new)
        .serialize(obj, type);
  }
}
