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

import static io.appulse.epmd.java.core.model.Tag.PORT_PLEASE2_REQUEST;
import static io.netty.channel.ChannelFutureListener.CLOSE;

import io.appulse.epmd.java.core.model.Tag;
import io.appulse.epmd.java.core.model.request.GetNodeInfo;
import io.appulse.epmd.java.core.model.request.Request;
import io.appulse.epmd.java.core.model.response.NodeInfo;
import io.appulse.epmd.java.server.command.server.Context;

import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 *
 * @author Artem Labazin
 * @since 0.4.0
 */
@Slf4j
class GetNodeInfoRequestHandler implements RequestHandler {

  @Override
  public void handle (@NonNull Request request, @NonNull ChannelHandlerContext requestContext, @NonNull Context serverState) {
    if (!(request instanceof GetNodeInfo)) {
      val message = String.format("Invalid request object:%n%s", request);
      log.error(message);
      throw new IllegalArgumentException(message);
    }

    val getNodeInfo = (GetNodeInfo) request;

    val node = serverState.getNodes()
        .get(getNodeInfo.getName());

    NodeInfo info;
    if (node == null) {
      info = NodeInfo.builder()
          .ok(false)
          .build();
    } else {
      info = NodeInfo.builder()
          .ok(true)
          .port(node.getPort())
          .type(node.getType())
          .protocol(node.getProtocol())
          .high(node.getHigh())
          .low(node.getLow())
          .name(node.getName())
          .build();
    }

    requestContext.writeAndFlush(info)
        .addListener(CLOSE);
  }

  @Override
  public Tag getTag () {
    return PORT_PLEASE2_REQUEST;
  }
}
