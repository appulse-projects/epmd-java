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

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.stream.Stream;

import io.appulse.epmd.java.core.mapper.FieldDescriptor;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class GuessMeFieldDeserializer implements FieldDeserializer<Object> {

  @Getter(lazy = true, value = PRIVATE)
  List<AbstractFieldDeserializer<?>> deserializers = createDeserializers();

  @Override
  public Object read (byte[] bytes, FieldDescriptor descriptor) {
    return getDeserializers()
        .stream()
        .filter(it -> it.isApplicable(descriptor))
        .findAny()
        .orElseThrow(RuntimeException::new)
        .read(bytes, descriptor);
  }

  private List<AbstractFieldDeserializer<?>> createDeserializers () {
    return Stream.of(
        NumberFieldDeserializer.class,
        NodeTypeFieldDeserializer.class,
        ProtocolFieldDeserializer.class,
        VersionFieldDeserializer.class,
        StringFieldDeserializer.class,
        IterableFieldDeserializer.class,
        BooleanFieldDeserializer.class,
        OptionalFieldDeserializer.class,
        EnumFieldDeserializer.class
    )
        .map(FieldDeserializerCache::get)
        .collect(toList());
  }
}
