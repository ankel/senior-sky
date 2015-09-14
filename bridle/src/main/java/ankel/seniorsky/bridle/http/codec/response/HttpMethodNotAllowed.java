package ankel.seniorsky.bridle.http.codec.response;

import java.util.Map;

/**
 * @author Binh Tran
 */
public final class HttpMethodNotAllowed extends AbstractHttpResponse
{
  public HttpMethodNotAllowed(final Map<String, String> headers, final byte[] body)
  {
    super(405, "Method Not Allowed", headers, body);
  }
}
