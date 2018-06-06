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

package io.appulse.epmd.java.core.model.response;

import lombok.ToString;

/**
 * Stop node response.
 *
 * @since 0.0.1
 * @author Artem Labazin
 */
@ToString
public enum StopResult {

  /**
   * Node was stopped.
   */
  STOPPED,

  /**
   * Node doesn't exist.
   */
  NOEXIST,

  /**
   * Unknown result.
   */
  UNKNOWN;
}
