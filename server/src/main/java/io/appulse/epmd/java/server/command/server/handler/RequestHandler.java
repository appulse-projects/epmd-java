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

package io.appulse.epmd.java.server.command.server.handler;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.server.command.server.Request;

import lombok.NonNull;

public interface RequestHandler {

  Map<Tag, RequestHandler> ALL = Stream.of(
      new GetEpmdDumpRequestHandler(),
      new GetEpmdInfoRequestHandler(),
      new GetNodeInfoRequestHandler(),
      new KillRequestHandler(),
      new RegistrationRequestHandler(),
      new StopRequestHandler()
  ).collect(toMap(RequestHandler::getTag, it -> it));

  void handle (@NonNull Request request);

  Tag getTag ();
}
