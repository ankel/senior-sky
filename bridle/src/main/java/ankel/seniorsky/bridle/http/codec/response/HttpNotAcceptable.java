package ankel.seniorsky.bridle.http.codec.response;

import java.util.Map;

/**
 * @author Binh Tran
 */
public final class HttpNotAcceptable extends AbstractHttpResponse
{
  public HttpNotAcceptable(final Map<String, String> headers, final byte[] body)
  {
    super(406, "Not Acceptable", headers, body);
  }
}
