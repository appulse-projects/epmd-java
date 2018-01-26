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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@Slf4j
public final class FieldDeserializerCache {

  private static final Map<Class<? extends FieldDeserializer<?>>, FieldDeserializer<?>> CACHE;

  private static final BiFunction<Class<? extends FieldDeserializer<?>>, FieldDeserializer<?>, FieldDeserializer<?>> COMPUTE;

  static {
    CACHE = new ConcurrentHashMap<>(20);
    COMPUTE = (key, value) -> {
      if (value != null) {
        return value;
      }
      try {
        return key.newInstance();
      } catch (IllegalAccessException | InstantiationException ex) {
        log.error("Error during instantiation", ex);
        throw new RuntimeException(ex);
      }
    };
  }

  @SuppressWarnings("unchecked")
  public static <T extends FieldDeserializer<?>> T get (Class<T> type) {
    return (T) CACHE.compute(type, COMPUTE);
  }

  private FieldDeserializerCache () {
  }
}
