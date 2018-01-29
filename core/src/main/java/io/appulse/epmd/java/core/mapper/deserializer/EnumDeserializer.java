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

package io.appulse.epmd.java.core.mapper.deserializer;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import io.appulse.epmd.java.core.mapper.deserializer.exception.DeserializationException;
import io.appulse.epmd.java.core.mapper.deserializer.exception.EnumUnknownValueException;
import io.appulse.utils.Bytes;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
class EnumDeserializer implements Deserializer {

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

  @Override
  @SuppressWarnings("unchecked")
  public <T> T deserialize (Bytes bytes, Class<T> type) throws DeserializationException {
    String string = bytes.getString(ISO_8859_1);
    T[] constants = type.getEnumConstants();

    Optional<Object> constant = findConstant(constants, string);
    if (constant.isPresent()) {
      return (T) constant.get();
    }

    Optional<Object> methodResult = getByMethod(type, string);
    if (methodResult.isPresent()) {
      return (T) methodResult.get();
    }

    Optional<T> undefined = Stream.of(constants)
        .filter(it -> ENUM_UNKNOWN_VALUE.contains(it.toString()))
        .findAny();

    return undefined
        .orElseThrow(() -> new EnumUnknownValueException(string));
  }

  @Override
  public boolean isApplicable (Class<?> type) {
    return type.isEnum();
  }

  private Optional<Object> findConstant (Object[] constants, String string) {
    return Stream.of(constants)
        .map(it -> (Enum<?>) it)
        .filter(it -> it.name().equals(string))
        .findAny()
        .map(it -> (Object) it);
  }

  private Optional<Object> getByMethod (Class<?> type, String string) {
    Optional<Method> optional = Stream.of(type.getDeclaredMethods())
        .filter(it -> Modifier.isStatic(it.getModifiers()))
        .filter(it -> Modifier.isPublic(it.getModifiers()))
        .filter(it -> it.getParameterCount() == 1)
        .filter(it -> it.getParameterTypes()[0] == String.class)
        .filter(it -> it.getReturnType() == type)
        .filter(it -> ENUM_CREATE_METHODS_NAMES.contains(it.getName()))
        .findAny();

    if (!optional.isPresent()) {
      return empty();
    }

    Method method = optional.get();
    Object result = null;
    try {
      result = method.invoke(null, string);
    } catch (IllegalAccessException | InvocationTargetException ex) {
      throw new DeserializationException(ex);
    }
    return ofNullable(result);
  }
}
