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

package io.appulse.epmd.java.core.mapper;

import static java.util.Optional.ofNullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import io.appulse.epmd.java.core.mapper.deserializer.field.FieldDeserializer;
import io.appulse.epmd.java.core.mapper.deserializer.field.FieldDeserializerCache;
import io.appulse.epmd.java.core.mapper.serializer.field.FieldSerializer;
import io.appulse.epmd.java.core.mapper.serializer.field.FieldSerializerCache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@Value
@Builder
@AllArgsConstructor
public final class FieldDescriptor {

  String name;

  int order;

  int length;

  ByteOrder byteOrder;

  FieldSerializer<?> serializer;

  FieldDeserializer<?> deserializer;

  boolean hasLengthBefore;

  Class<?> type;

  Type genericType;

  @SneakyThrows
  public FieldDescriptor (Field field) {
    val annotation = ofNullable(field.getAnnotation(io.appulse.epmd.java.core.mapper.Field.class))
        .orElseThrow(RuntimeException::new);

    name = field.getName();
    order = annotation.order();
    byteOrder = annotation.byteOrder();
    serializer = FieldSerializerCache.get(annotation.serializer());
    deserializer = FieldDeserializerCache.get(annotation.deserializer());

    val lengthBefore = field.getAnnotation(LengthBefore.class);
    if (lengthBefore == null) {
      length = annotation.bytes();
      hasLengthBefore = false;
    } else {
      length = lengthBefore.bytes();
      hasLengthBefore = true;
    }

    type = field.getType();
    genericType = field.getGenericType();
  }

  public boolean isOptional () {
    return Optional.class.isAssignableFrom(type);
  }

  public boolean isGenericContainer () {
    return genericType instanceof ParameterizedType;
  }

  public Class<?> getFirstGenericType () {
    return getGenericType(0);
  }

  public Class<?> getGenericType (int index) {
    return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[index];
  }
}
