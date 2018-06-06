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

package io.appulse.epmd.java.server.command.server.handler.command;

import static java.util.stream.Collectors.toConcurrentMap;

import java.util.Map;
import java.util.stream.Stream;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.request.Request;
import io.appulse.epmd.java.server.command.server.ServerState;

import io.netty.channel.ChannelHandlerContext;

/**
 * Request handler API.
 *
 * @since 0.4.0
 * @author Artem Labazin
 */
public interface RequestHandler {

  /**
   * All known request handlers.
   */
  @SuppressWarnings("PMD.UseConcurrentHashMap") // already use
  Map<Tag, RequestHandler> ALL = Stream.of(
      new GetEpmdDumpRequestHandler(),
      new GetEpmdInfoRequestHandler(),
      new GetNodeInfoRequestHandler(),
      new KillRequestHandler(),
      new RegistrationRequestHandler(),
      new StopRequestHandler()
  ).collect(toConcurrentMap(RequestHandler::getTag, it -> it));

  /**
   * Main request handler method.
   *
   * @param request parsed request object
   *
   * @param context request context
   *
   * @param state current EPMD server state
   */
  void handle (Request request, ChannelHandlerContext context, ServerState state);

  /**
   * Getting request tag, which handler is able to handle.
   *
   * @return supported {@link Tag} by this handler
   */
  Tag getTag ();
}
