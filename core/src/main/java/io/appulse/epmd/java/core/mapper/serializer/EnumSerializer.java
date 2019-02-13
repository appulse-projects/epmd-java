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

import static java.nio.charset.StandardCharsets.ISO_8859_1;

import io.appulse.epmd.java.core.mapper.serializer.exception.SerializationException;

import lombok.NonNull;
import lombok.val;

/**
 * Implementation for serializing enum values as {@link String}.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
class EnumSerializer implements Serializer {

  @Override
  public byte[] serialize (@NonNull Object object, @NonNull Class<?> type) throws SerializationException {
    val string = ((Enum<?>) object).name();
    return string.getBytes(ISO_8859_1);
  }

  @Override
  public boolean isApplicable (@NonNull Class<?> type) {
    return type.isEnum();
  }
}
