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

import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.core.mapper.serializer.field.EnumFieldSerializer;
import io.appulse.epmd.java.core.mapper.serializer.field.FieldSerializerCache;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
class EnumSerializer implements Serializer {

  @Getter(lazy = true, value = PRIVATE)
  EnumFieldSerializer enumFieldSerializer = createEnumFieldSerializer();

  @Override
  public byte[] serialize (Object object, Class<?> type) throws Exception {
    return getEnumFieldSerializer().serialize(object);
  }

  @Override
  public boolean isApplicable (Class<?> type) {
    return type.isEnum();
  }

  private EnumFieldSerializer createEnumFieldSerializer () {
    return FieldSerializerCache.get(EnumFieldSerializer.class);
  }
}
