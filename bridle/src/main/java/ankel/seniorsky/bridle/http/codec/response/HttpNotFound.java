package ankel.seniorsky.bridle.http.codec.response;

import java.util.Map;

/**
 * @author Binh Tran
 */
public final class HttpNotFound extends AbstractHttpResponse
{
  public HttpNotFound(final Map<String, String> headers, final byte[] body)
  {
    super(404, "Not Found", headers, body);
  }
}
