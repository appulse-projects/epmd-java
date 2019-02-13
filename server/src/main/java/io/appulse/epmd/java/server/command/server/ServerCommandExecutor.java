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

package io.appulse.epmd.java.server.command.server;

import static io.netty.channel.ChannelOption.SO_BACKLOG;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;
import static io.netty.channel.ChannelOption.TCP_NODELAY;
import static java.util.Optional.of;
import static lombok.AccessLevel.PRIVATE;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.appulse.epmd.java.server.cli.CommonOptions;
import io.appulse.epmd.java.server.command.AbstractCommandExecutor;
import io.appulse.epmd.java.server.command.CommandOptions;
import io.appulse.epmd.java.server.command.server.handler.RequestDecoder;
import io.appulse.epmd.java.server.command.server.handler.ResponseEncoder;
import io.appulse.epmd.java.server.command.server.handler.ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Server running command executor.
 *
 * @since 0.3.2
 * @author Artem Labazin
 */
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ServerCommandExecutor extends AbstractCommandExecutor implements Closeable {

  ServerState serverState;

  EventLoopGroup bossGroup;

  EventLoopGroup workerGroup;

  AtomicBoolean closed = new AtomicBoolean(false);

  /**
   * Constructor.
   *
   * @param commonOptions common command options for all commands
   *
   * @param options specific command options
   */
  @SneakyThrows
  public ServerCommandExecutor (CommonOptions commonOptions, @NonNull CommandOptions options) {
    super(commonOptions);
    val serverOptions = of(options)
        .filter(it -> it instanceof ServerCommandOptions)
        .map(it -> (ServerCommandOptions) it)
        .orElse(new ServerCommandOptions());

    serverState = ServerState.builder()
        .nodes(new ConcurrentHashMap<>())
        .commonOptions(commonOptions)
        .serverOptions(serverOptions)
        .build();

    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup(2);
  }

  @Override
  public void execute () {
    log.info("Starting server on port {}", getPort());
    try {
      new ServerBootstrap()
          .group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel (SocketChannel channel) throws Exception {
              channel.pipeline()
                  .addLast("decoder", new RequestDecoder())
                  .addLast("encoder", new ResponseEncoder())
                  .addLast("handler", new ServerHandler(serverState));
            }
          })
          .option(SO_BACKLOG, 128)
          .childOption(SO_KEEPALIVE, true)
          .childOption(TCP_NODELAY, true)
          .bind(getPort())
          .sync()
          // Wait until the server socket is closed.
          .channel().closeFuture().sync();
    } catch (InterruptedException ex) {
      log.error("Server work exception", ex);
    } finally {
      close();
    }
  }

  @Override
  @SneakyThrows
  public void close () {
    if (closed.get()) {
      log.debug("Server was already closed");
      return;
    }
    closed.set(true);

    workerGroup.shutdownGracefully();
    bossGroup.shutdownGracefully();
    log.debug("Boss and workers groups are closed");

    serverState.getNodes().clear();
    log.debug("All nodes are removed");

    log.info("Server was closed");
  }
}
