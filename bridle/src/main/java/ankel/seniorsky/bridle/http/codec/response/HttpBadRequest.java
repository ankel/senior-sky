package ankel.seniorsky.bridle.http.codec.response;

import java.util.Map;

/**
 * @author Binh Tran
 */
public class HttpBadRequest extends AbstractHttpResponse
{
  public HttpBadRequest(final Map<String, String> headers, final byte[] body)
  {
    super(400, "Bad Request", headers, body);
  }
}
