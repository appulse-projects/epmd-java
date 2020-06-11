/*
 * Copyright 2020 the original author or authors.
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

package io.appulse.epmd.java.client;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.net.InetAddress;
import java.util.function.Supplier;

import io.appulse.epmd.java.core.model.request.Request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * A command's common fields holder class.
 *
 * @param <R> the type of command's request
 *
 * @param <T> the type of response supplied by this command
 *
 * @since 2.0.0
 * @author Artem Labazin
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = PROTECTED)
@FieldDefaults(level = PRIVATE, makeFinal = true)
abstract class CommandAbstract<R extends Request, T> implements Supplier<T> {

  @NonNull
  InetAddress address;

  @NonNull
  Integer port;

  @NonNull
  R request;

  /**
   * Creates a remote connection object.
   *
   * @return the new connection instance
   */
  protected Connection createConnection () {
    return new Connection(address, port);
  }

  /**
   * Returns a request's byte array representation.
   *
   * @return the request's byte array representation
   */
  protected byte[] getRequestBytes () {
    return request.toBytes();
  }
}
