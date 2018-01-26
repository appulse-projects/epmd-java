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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import io.appulse.epmd.java.core.mapper.FieldDescriptor;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
public final class BooleanFieldDeserializer extends AbstractFieldDeserializer<Boolean> {

  @Override
  public Boolean read (byte[] bytes, FieldDescriptor descriptor) {
    return bytes[0] == 0
         ? TRUE
         : FALSE;
  }

  @Override
  boolean isApplicable (FieldDescriptor descriptor) {
    return descriptor.getType() == Boolean.TYPE;
  }
}
