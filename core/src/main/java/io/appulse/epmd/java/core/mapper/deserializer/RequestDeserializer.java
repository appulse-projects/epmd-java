/*
 * Copyright 2019 the original author or authors.
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

import io.appulse.epmd.java.core.mapper.deserializer.exception.DeserializationException;
import io.appulse.epmd.java.core.mapper.deserializer.exception.InvalidReceivedMessageLengthException;
import io.appulse.epmd.java.core.mapper.deserializer.exception.InvalidReceivedMessageTagException;
import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.request.Request;
import io.appulse.utils.Bytes;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Implementation for deserializing POJOs
 * which implements {@link Request} interface.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@Slf4j
class RequestDeserializer implements Deserializer {

  @Override
  public <T> T deserialize (@NonNull Bytes bytes, @NonNull Class<T> type) throws DeserializationException {
    val length = bytes.getShort();
    if (length != bytes.remaining()) {
      val message = String.format("Expected length is %d - %d bytes, but actual length is %d bytes.",
                                  2, length, bytes.remaining());
      log.error(message);
      throw new InvalidReceivedMessageLengthException(message);
    }

    T result;
    try {
      result = type.newInstance();
    } catch (IllegalAccessException | InstantiationException ex) {
      log.error("Deserialized type instantiation error", ex);
      throw new DeserializationException(ex);
    }

    if (!(result instanceof Request)) {
      val message = String.format("Deserializing type '%s' is not an instance of '%s'",
                                  type.getSimpleName(), Request.class.getSimpleName());
      log.error(message);
      throw new DeserializationException(message);
    }
    val request = (Request) result;

    val tag = Tag.of(bytes.getByte());
    if (tag != request.getTag()) {
      val message = String.format("Expected tag is: %s, but actual tag is: %s",
                                  request.getTag(), tag);
      log.error(message);
      throw new InvalidReceivedMessageTagException(message);
    }

    request.read(bytes);
    return result;
  }

  @Override
  public boolean isApplicable (@NonNull Class<?> type) {
    return Request.class.isAssignableFrom(type);
  }
}
