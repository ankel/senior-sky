package ankel.seniorsky.bridle.http.handler;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ankel.seniorsky.bridle.http.codec.request.GetRequest;
import ankel.seniorsky.bridle.http.codec.request.InvalidRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Binh Tran
 */
public class BridleHttpRequestDecoderTest
{
  private BridleHttpRequestDecoder decoder = new BridleHttpRequestDecoder();

  public ByteBuf buf;

  @Test
  public void testDecode() throws Exception
  {
    setupBufFor("GET / HTTP/1.1 \r\n\r\n");
    List<Object> ret = new ArrayList<>();
    decoder.decode(null, buf, ret);

    assertEquals(1, ret.size());
    assertTrue(ret.get(0) instanceof GetRequest);
    assertEquals("/", ((GetRequest) ret.get(0)).getUri());
  }

  @Test
  public void testDecodeRelativePath() throws Exception
  {
    // new line character & trailing space
    setupBufFor("GET /bahh HTTP/1.1  \r\n\r\n");
    List<Object> ret = new ArrayList<>();
    decoder.decode(null, buf, ret);

    assertEquals(1, ret.size());
    assertTrue(ret.get(0) instanceof GetRequest);
    assertEquals("/", ((GetRequest) ret.get(0)).getUri());
  }

  @Test
  public void testInvalidDecode() throws Exception
  {
    // Reject not supported method request
    setupBufFor("POST / HTTP/1.1\r\n\r\n");
    List<Object> ret = new ArrayList<>();
    decoder.decode(null, buf, ret);

    assertEquals(1, ret.size());
    assertTrue(ret.get(0) instanceof InvalidRequest);
    assertEquals(InvalidRequest.Reason.METHOD_NOT_ALLOWED,
        ((InvalidRequest) ret.get(0)).getReason());

    // Reject malformed request line
    setupBufFor("POST  HTTP/1.1\r\n\r\n");
    ret = new ArrayList<>();
    decoder.decode(null, buf, ret);

    assertEquals(1, ret.size());
    assertTrue(ret.get(0) instanceof InvalidRequest);
    assertEquals(InvalidRequest.Reason.BAD_REQUEST_LINE,
        ((InvalidRequest) ret.get(0)).getReason());

    // Reject absolute uri request
    setupBufFor("GET http://www.google.com HTTP/1.1\r\n\r\n");
    ret = new ArrayList<>();
    decoder.decode(null, buf, ret);

    assertEquals(1, ret.size());
    assertTrue(ret.get(0) instanceof InvalidRequest);
    assertEquals(InvalidRequest.Reason.NOT_RELATIVE_URI,
        ((InvalidRequest) ret.get(0)).getReason());
  }

  @Test
  public void testHeader() throws Exception
  {
    setupBufFor("GET / HTTP/1.1 \r\n" +
        "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8," +
        "image/png,*/*;q=0.5\r\n\r\n");
    List<Object> ret = new ArrayList<>();
    decoder.decode(null, buf, ret);

    assertEquals(1, ret.size());
    assertTrue(ret.get(0) instanceof GetRequest);
    GetRequest getRequest = (GetRequest) ret.get(0);
    final List<String> values = getRequest.getHeaders().get("accept");

    assertTrue(values.contains("text/xml"));
    assertTrue(values.contains("application/xml"));
    assertTrue(values.contains("application/xhtml+xml"));
    assertTrue(values.contains("text/html"));
    assertTrue(values.contains("text/plain"));
    assertTrue(values.contains("image/png"));
    assertTrue(values.contains("*/*"));
  }

  @Test
  public void testHeaderMultiline() throws Exception
  {
    setupBufFor("GET / HTTP/1.1 \r\n" +
        "Accept: text/xml,application/xml,application/xhtml+xml \r\n" +
        "accEPT: text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\r\n\r\n");
    List<Object> ret = new ArrayList<>();
    decoder.decode(null, buf, ret);

    assertEquals(1, ret.size());
    assertTrue(ret.get(0) instanceof GetRequest);
    GetRequest getRequest = (GetRequest) ret.get(0);
    final List<String> values = getRequest.getHeaders().get("accept");

    assertTrue(values.contains("text/xml"));
    assertTrue(values.contains("application/xml"));
    assertTrue(values.contains("application/xhtml+xml"));
    assertTrue(values.contains("text/html"));
    assertTrue(values.contains("text/plain"));
    assertTrue(values.contains("image/png"));
    assertTrue(values.contains("*/*"));
  }

  private void setupBufFor(final String s)
  {
    buf = Unpooled.buffer();
    buf.writeBytes(s.getBytes());
  }
}
