package ankel.seniorsky.bridle.http.codec.response;

import java.util.Map;

/**
 * @author Binh Tran
 */
public final class HttpOk extends AbstractHttpResponse
{
  public HttpOk(final Map<String, String> headers, final byte[] body)
  {
    super(200, "OK", headers, body);
  }
}
