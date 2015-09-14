package ankel.seniorsky.bridle;

import lombok.RequiredArgsConstructor;

import ankel.seniorsky.bridle.http.codec.visitor.HttpRequestVisitor;
import ankel.seniorsky.bridle.http.handler.BridleHttpRequestDecoder;
import ankel.seniorsky.bridle.http.handler.BridleHttpRequestHandler;
import ankel.seniorsky.bridle.http.handler.BridleHttpResponseEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Setup netty pipeline
 *
 * @author Binh Tran
 */
@RequiredArgsConstructor
public class BridleServerChannelInitializer extends ChannelInitializer<SocketChannel>
{
  private final HttpRequestVisitor httpRequestVisitor;

  @Override
  protected void initChannel(final SocketChannel ch) throws Exception
  {
    ChannelPipeline pipeline = ch.pipeline();

    pipeline.addLast(new BridleHttpRequestDecoder());
    // But you really really really should have done this
    //    p.addLast(new HttpRequestDecoder());

    pipeline.addLast(new BridleHttpResponseEncoder());
    pipeline.addLast(new BridleHttpRequestHandler(httpRequestVisitor));
  }
}
