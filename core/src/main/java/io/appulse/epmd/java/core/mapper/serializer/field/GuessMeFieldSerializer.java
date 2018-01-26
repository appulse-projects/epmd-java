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

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class GuessMeFieldSerializer implements FieldSerializer<Object> {

  @Getter(lazy = true, value = PRIVATE)
  List<AbstractFieldSerializer<?>> serializers = createSerializers();

  @Override
  public byte[] write (Object value) {
    return getSerializers().stream()
        .filter(it -> it.isApplicable(value))
        .findAny()
        .orElseThrow(RuntimeException::new)
        .serialize(value);
  }

  private List<AbstractFieldSerializer<?>> createSerializers () {
    return Stream.of(
        NumberFieldSerializer.class,
        NodeTypeFieldSerializer.class,
        ProtocolFieldSerializer.class,
        VersionFieldSerializer.class,
        StringFieldSerializer.class,
        IterableFieldSerializer.class,
        BooleanFieldSerializer.class,
        OptionalFieldSerializer.class
    )
        .map(FieldSerializerCache::get)
        .collect(toList());
  }
}
