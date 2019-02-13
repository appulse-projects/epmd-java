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

package io.appulse.epmd.java.core.mapper.serializer;

import io.appulse.epmd.java.core.mapper.serializer.exception.SerializationException;
import io.appulse.epmd.java.core.model.request.Request;
import io.appulse.utils.Bytes;

import lombok.NonNull;
import lombok.val;

/**
 * Implementation for serializing POJOs
 * which implements {@link Request} interface.
 *
 * @since 0.4.0
 * @author Artem Labazin
 */
class RequestSerializer implements Serializer {

  @Override
  public byte[] serialize (@NonNull Object object, @NonNull Class<?> type) throws SerializationException {
    val request = (Request) object;

    val bytes = Bytes.allocate()
        .put2B(0)
        .put1B(request.getTag().getCode());

    request.write(bytes);

    return bytes
        .put2B(0, bytes.limit() - Short.BYTES)
        .array();
  }

  @Override
  public boolean isApplicable (@NonNull Class<?> type) {
    return Request.class.isAssignableFrom(type);
  }
}
