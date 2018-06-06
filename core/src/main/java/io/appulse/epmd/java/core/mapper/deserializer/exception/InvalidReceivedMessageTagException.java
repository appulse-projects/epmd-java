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

package io.appulse.epmd.java.core.mapper.deserializer.exception;

/**
 * Exception, which tells about discrepancy with expected and parsed message tag.
 *
 * @since 0.1.0
 * @author Artem Labazin
 */
public class InvalidReceivedMessageTagException extends DeserializationException {

  private static final long serialVersionUID = 7489623306319379817L;

  /**
   * Constructs a new runtime exception with null as its detail message.
   * <p>
   * The cause is not initialized, and may subsequently be initialized
   * by a call to Throwable.initCause(java.lang.Throwable).
   */
  public InvalidReceivedMessageTagException () {
    super();
  }

  /**
   * Constructs a new runtime exception with the specified detail message.
   * <p>
   * The cause is not initialized, and may subsequently be initialized
   * by a call to Throwable.initCause(java.lang.Throwable).
   *
   * @param message the detail message.
   */
  public InvalidReceivedMessageTagException (String message) {
    super(message);
  }

  /**
   * Constructs a new runtime exception with the specified detail message.
   * <p>
   * The cause is not initialized, and may subsequently be initialized
   * by a call to Throwable.initCause(java.lang.Throwable).
   *
   * @param message the detail message.
   *
   * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
   *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public InvalidReceivedMessageTagException (String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new runtime exception with the specified cause and a detail message
   * of (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
   * <p>
   * This constructor is useful for runtime exceptions that are little more than wrappers for other throwables.
   *
   * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
   *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public InvalidReceivedMessageTagException (Throwable cause) {
    super(cause);
  }
}
