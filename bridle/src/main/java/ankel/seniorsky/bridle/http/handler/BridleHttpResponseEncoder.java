package ankel.seniorsky.bridle.http.handler;

import ankel.seniorsky.bridle.http.codec.response.AbstractHttpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Turn {@link AbstractHttpResponse} to byte to be written out. Doesn't support chunk encoding
 *
 * @author Binh Tran
 */
public class BridleHttpResponseEncoder extends MessageToByteEncoder<AbstractHttpResponse>
{
  @Override
  protected void encode(final ChannelHandlerContext ctx, final AbstractHttpResponse msg,
      final ByteBuf out)
      throws Exception
  {
    out.writeBytes(msg.getBytes());
  }
}
