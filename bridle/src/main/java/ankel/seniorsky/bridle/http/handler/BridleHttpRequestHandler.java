package ankel.seniorsky.bridle.http.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ankel.seniorsky.bridle.http.codec.request.HttpRequest;
import ankel.seniorsky.bridle.http.codec.response.AbstractHttpResponse;
import ankel.seniorsky.bridle.http.codec.response.HttpInternalServerError;
import ankel.seniorsky.bridle.http.codec.visitor.HttpRequestVisitor;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Binh Tran
 */
@Slf4j
@RequiredArgsConstructor
public class BridleHttpRequestHandler extends SimpleChannelInboundHandler<HttpRequest>
{
  private final HttpRequestVisitor httpRequestVisitor;

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final HttpRequest msg)
      throws Exception
  {
    ctx.writeAndFlush(msg.accept(httpRequestVisitor))
        .addListener(ChannelFutureListener.CLOSE);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
      throws Exception
  {
    log.error("Exception caught while processing request", cause);
    ctx.writeAndFlush(
        new HttpInternalServerError(
            AbstractHttpResponse.EMPTY_HEADERS,
            AbstractHttpResponse.EMPTY_BODY))
        .addListener(ChannelFutureListener.CLOSE);
  }
}
