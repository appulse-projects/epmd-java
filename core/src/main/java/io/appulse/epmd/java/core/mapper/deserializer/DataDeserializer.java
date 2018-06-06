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
import io.appulse.epmd.java.core.mapper.deserializer.exception.DeserializationException;
import io.appulse.epmd.java.core.mapper.deserializer.exception.InvalidReceivedMessageTagException;
import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.TaggedMessage;
import io.appulse.utils.Bytes;

import lombok.NonNull;
import lombok.val;

/**
 * Implementation for deserializing POJOs
 * which implements {@link DataSerializable} interface.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
class DataDeserializer implements Deserializer {

  @Override
  public <T> T deserialize (@NonNull Bytes bytes, @NonNull Class<T> type) throws DeserializationException {
    T result;
    try {
      result = type.newInstance();
    } catch (IllegalAccessException | InstantiationException ex) {
      throw new DeserializationException(ex);
    }

    if (result instanceof TaggedMessage) {
      val expectedTag = ((TaggedMessage) result).getTag();
      val tag = Tag.of(bytes.getByte());
      if (!expectedTag.equals(tag)) {
        val message = String.format("Expected tag is: %s, but actual tag is: %s",
                                    expectedTag, tag);
        throw new InvalidReceivedMessageTagException(message);
      }
    }

    ((DataSerializable) result).read(bytes);
    return result;
  }

  @Override
  public boolean isApplicable (@NonNull Class<?> type) {
    return DataSerializable.class.isAssignableFrom(type);
  }
}
