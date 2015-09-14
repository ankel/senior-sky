package ankel.seniorsky.bridle.http.codec.response;

import java.util.Map;

/**
 * @author Binh Tran
 */
public class HttpRequestEntityTooLarge extends AbstractHttpResponse
{
  public HttpRequestEntityTooLarge(final Map<String, String> headers, final byte[] body)
  {
    super(413, "Request Entity Too Large", headers, body);
  }
}
