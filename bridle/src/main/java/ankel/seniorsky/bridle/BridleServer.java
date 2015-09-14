package ankel.seniorsky.bridle;

import java.nio.file.Path;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import ankel.seniorsky.bridle.file.FileOpsHttpRequestVisitor;
import ankel.seniorsky.bridle.file.SimpleFileOperation;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Binh Tran
 */
@RequiredArgsConstructor
@Slf4j
public class BridleServer implements Runnable
{
  public static final int WORkER_THREAD_COUNT = 30;
  public static final long MAX_SIZE = 2048;

  private final Path root;
  private final int serverPort;
  private final EventLoopGroup workerEventLoopGroup =
      new NioEventLoopGroup(WORkER_THREAD_COUNT,
          new ThreadFactoryBuilder()
              .setDaemon(true)
              .setNameFormat("seniorsky-bridle-worker-%d")
              .build());

  @Override
  public void run()
  {
    try
    {
      ServerBootstrap bootstrap = new ServerBootstrap();

      bootstrap.group(workerEventLoopGroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 10 * WORkER_THREAD_COUNT)
          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
          .childHandler(new BridleServerChannelInitializer(
              new FileOpsHttpRequestVisitor(
                  new SimpleFileOperation(root, MAX_SIZE))));

      ChannelFuture channelFuture = bootstrap.bind(serverPort);
      channelFuture.addListener((f) ->
      {
        if (f.isSuccess())
        {
          log.info("Server started successfully on localhost:{}", serverPort);
        }
      });
      Channel channel = channelFuture.sync().channel();
      channel.closeFuture().sync();
    }
    catch (InterruptedException e)
    {
      // no op
    }
    finally
    {
      workerEventLoopGroup.shutdownGracefully();
    }
  }

  public void shutdown()
  {
    workerEventLoopGroup.shutdownGracefully();
  }
}
