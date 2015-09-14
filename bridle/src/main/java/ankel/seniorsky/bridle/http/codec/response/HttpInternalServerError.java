package ankel.seniorsky.bridle.http.codec.response;

import java.util.Map;

/**
 * @author Binh Tran
 */
public final class HttpInternalServerError extends AbstractHttpResponse
{
  public HttpInternalServerError(final Map<String, String> headers, final byte[] body)
  {
    super(500, "Internal Server Error", headers, body);
  }
}
