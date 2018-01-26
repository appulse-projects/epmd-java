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

package io.appulse.epmd.java.core.mapper.serializer.field;

import java.util.Optional;

import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public final class OptionalFieldSerializer extends AbstractFieldSerializer<Optional<?>> {

  @Override
  public byte[] write (Optional<?> optional) {
    if (!optional.isPresent()) {
      return new byte[0];
    }

    val subValue = optional.get();
    return FieldSerializerCache.get(GuessMeFieldSerializer.class)
        .write(subValue);
  }

  @Override
  boolean isApplicable (Object value) {
    return value instanceof Optional;
  }
}
