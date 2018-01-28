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

import io.appulse.epmd.java.core.mapper.DataSerializable;
import io.appulse.utils.Bytes;

/**
 *
 * @author Artem Labazin
 * @since 0.0.1
 */
class DataDeserializer implements Deserializer {

  @Override
  public <T> T deserialize (Bytes bytes, Class<T> type) throws Exception {
    T result = type.newInstance();
    ((DataSerializable) result).read(bytes);
    return result;
  }

  @Override
  public boolean isApplicable (Class<?> type) {
    return DataSerializable.class.isAssignableFrom(type);
  }
}
