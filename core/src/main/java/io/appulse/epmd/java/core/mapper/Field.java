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

import static io.appulse.epmd.java.core.mapper.ByteOrder.BIG_ENDIAN;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.appulse.epmd.java.core.mapper.deserializer.field.FieldDeserializer;
import io.appulse.epmd.java.core.mapper.deserializer.field.GuessMeFieldDeserializer;
import io.appulse.epmd.java.core.mapper.serializer.field.FieldSerializer;
import io.appulse.epmd.java.core.mapper.serializer.field.GuessMeFieldSerializer;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@Documented
@Target(FIELD)
@Retention(RUNTIME)
public @interface Field {

  int order () default -1;

  int bytes () default -1;

  ByteOrder byteOrder () default BIG_ENDIAN;

  Class<? extends FieldSerializer<?>> serializer () default GuessMeFieldSerializer.class;

  Class<? extends FieldDeserializer<?>> deserializer () default GuessMeFieldDeserializer.class;
}
