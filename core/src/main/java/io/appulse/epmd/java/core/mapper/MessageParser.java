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

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public final class MessageParser {

  private static final Map<Class<?>, List<FieldDescriptor>> CACHE;

  private static final BiFunction<Class<?>, List<FieldDescriptor>, List<FieldDescriptor>> COMPUTE;

  static {
    CACHE = new ConcurrentHashMap<>(20);
    COMPUTE = (key, value) -> {
      if (value != null) {
        return value;
      }
      return Stream.of(key.getDeclaredFields())
          .filter(it -> it.isAnnotationPresent(Field.class))
          .map(FieldDescriptor::new)
          .sorted(Comparator.comparing(FieldDescriptor::getOrder))
          .collect(toList());
    };
  }

  public static List<FieldDescriptor> parse (Class<?> type) {
    return CACHE.compute(type, COMPUTE);
  }

  private MessageParser () {
  }
}
