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

package io.appulse.epmd.java.core.model;

/**
 * Interface for serializable messages, which support {@link Tag}.
 *
 * @since 0.4.0
 * @author Artem Labazin
 */
public interface TaggedMessage {

  /**
   * Returns message's tag.
   *
   * @return message's {@link Tag}.
   */
  Tag getTag ();
}
