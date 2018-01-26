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

import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import io.appulse.epmd.java.core.mapper.FieldDescriptor;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class EnumFieldDeserializer extends AbstractFieldDeserializer<Object> {

  private static final Set<String> ENUM_CREATE_METHODS_NAMES;

  private static final Set<String> ENUM_UNKNOWN_VALUE;

  static {
    ENUM_CREATE_METHODS_NAMES = Collections.synchronizedSet(new HashSet<>(asList(
        "of",
        "parse",
        "from"
    )));
    ENUM_UNKNOWN_VALUE = Collections.synchronizedSet(new HashSet<>(asList(
        "UNDEFINED",
        "UNKNOWN"
    )));
  }

  @Getter(lazy = true, value = PRIVATE)
  StringFieldDeserializer stringFieldDeserializer = createStringFieldDeserializer();

  @Override
  @SneakyThrows
  public Object read (byte[] bytes, FieldDescriptor descriptor) {
    val str = getStringFieldDeserializer().read(bytes, descriptor);

    val constants = descriptor.getType().getEnumConstants();
    Optional<Object> constant = Stream.of(constants)
        .map(it -> (Enum<?>) it)
        .filter(it -> it.name().equals(str))
        .findAny()
        .map(it -> (Object) it);

    if (constant.isPresent()) {
      return constant.get();
    }

    Optional<Method> optional = Stream.of(descriptor.getType().getDeclaredMethods())
        .filter(it -> Modifier.isStatic(it.getModifiers()))
        .filter(it -> Modifier.isPublic(it.getModifiers()))
        .filter(it -> it.getParameterCount() == 1)
        .filter(it -> it.getParameterTypes()[0] == String.class)
        .filter(it -> it.getReturnType() == descriptor.getType())
        .filter(it -> ENUM_CREATE_METHODS_NAMES.contains(it.getName()))
        .findAny();

    if (optional.isPresent()) {
      Method method = optional.get();
      return method.invoke(null, str);
    }

    Optional<Object> undefined = Stream.of(constants)
        .filter(it -> ENUM_UNKNOWN_VALUE.contains(it.toString()))
        .findAny();

    return undefined
        .orElseThrow(() -> new RuntimeException("Unknown enum value " + str));
  }

  @Override
  boolean isApplicable (FieldDescriptor descriptor) {
    return descriptor.getType().isEnum();
  }

  private StringFieldDeserializer createStringFieldDeserializer () {
    return FieldDeserializerCache.get(StringFieldDeserializer.class);
  }
}
