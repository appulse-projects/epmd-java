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

package io.appulse.epmd.java.core.mapper.deserializer.field;

import java.util.Optional;

import io.appulse.epmd.java.core.mapper.FieldDescriptor;

import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public final class OptionalFieldDeserializer extends AbstractFieldDeserializer<Optional<?>> {

  @Override
  public Optional<?> read (byte[] bytes, FieldDescriptor descriptor) {
    Class<?> type = descriptor.getFirstGenericType();
    val fakeDescriptor = FieldDescriptor.builder()
        .type(type)
        .build();

    val subValue = FieldDeserializerCache.get(GuessMeFieldDeserializer.class)
        .read(bytes, fakeDescriptor);

    return Optional.of(subValue);
  }

  @Override
  boolean isApplicable (FieldDescriptor descriptor) {
    return Optional.class.isAssignableFrom(descriptor.getType());
  }
}
