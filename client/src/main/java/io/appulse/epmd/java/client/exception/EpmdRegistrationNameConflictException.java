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

package io.appulse.epmd.java.client.exception;

import static java.util.Locale.ENGLISH;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Exception for case, when a node with such name is already exist.
 *
 * @since 2.0.0
 * @author Artem Labazin
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class EpmdRegistrationNameConflictException extends EpmdRegistrationException {

  private static final long serialVersionUID = 1059576272321883883L;

  String name;

  /**
   * Constructor.
   *
   * @param name a registration node name
   */
  public EpmdRegistrationNameConflictException (String name) {
    super();
    this.name = name;
  }

  @Override
  public String getMessage () {
    return String.format(ENGLISH, "A node with the name '%s' already exists", name);
  }
}
