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

import static lombok.AccessLevel.PRIVATE;

import io.appulse.epmd.java.core.mapper.FieldDescriptor;
import io.appulse.epmd.java.core.model.Protocol;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class ProtocolFieldDeserializer extends AbstractFieldDeserializer<Protocol> {

  @Getter(lazy = true, value = PRIVATE)
  NumberFieldDeserializer numberFieldDeserializer = createNumberFieldDeserializer();

  @Override
  public Protocol read (byte[] bytes, FieldDescriptor descriptor) {
    byte code = (byte) getNumberFieldDeserializer().read(bytes, Byte.class);
    return Protocol.of(code);
  }

  @Override
  boolean isApplicable (FieldDescriptor descriptor) {
    return Protocol.class.isAssignableFrom(descriptor.getType());
  }

  private NumberFieldDeserializer createNumberFieldDeserializer () {
    return FieldDeserializerCache.get(NumberFieldDeserializer.class);
  }
}
