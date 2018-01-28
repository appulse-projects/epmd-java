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

package io.appulse.epmd.java.core.mapper.serializer;

import io.appulse.epmd.java.core.mapper.DataSerializable;
import io.appulse.utils.Bytes;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.0.2
 */
class DataSerializer implements Serializer {

  @Override
  public byte[] serialize (Object object, Class<?> type) throws Exception {
    val body = Bytes.allocate();
    ((DataSerializable) object).write(body);
    return body.array();
  }

  @Override
  public boolean isApplicable (Class<?> type) {
    return DataSerializable.class.isAssignableFrom(type);
  }
}
