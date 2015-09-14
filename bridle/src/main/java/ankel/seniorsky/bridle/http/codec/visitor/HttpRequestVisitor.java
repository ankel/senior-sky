package ankel.seniorsky.bridle.http.codec.visitor;

import java.nio.charset.Charset;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;

import ankel.seniorsky.bridle.http.codec.request.GetRequest;
import ankel.seniorsky.bridle.http.codec.request.InvalidRequest;
import ankel.seniorsky.bridle.http.codec.response.AbstractHttpResponse;
import ankel.seniorsky.bridle.http.codec.response.HttpBadRequest;
import ankel.seniorsky.bridle.http.codec.response.HttpMethodNotAllowed;

/**
 * @author Binh Tran
 */
public abstract class HttpRequestVisitor
{
  public static final Charset ISO_8859 = Charset.forName("ISO-8859-1");

  public static final Map<String, String> ALLOW_GET_HEADER =
      ImmutableMap.of(HttpHeaders.ALLOW, "GET");

  public abstract AbstractHttpResponse visit(GetRequest getRequest);

  public AbstractHttpResponse visit(InvalidRequest invalidRequest)
  {
    switch (invalidRequest.getReason())
    {
      case BAD_REQUEST_LINE:
        return new HttpBadRequest(
            AbstractHttpResponse.EMPTY_HEADERS,
            "Reason: BAD_REQUEST_LINE".getBytes(ISO_8859));
      case NOT_RELATIVE_URI:
        return new HttpBadRequest(
            AbstractHttpResponse.EMPTY_HEADERS,
            "Reason: NOT_RELATIVE_URI".getBytes(ISO_8859));
      case METHOD_NOT_ALLOWED:
        return new HttpMethodNotAllowed(
            ALLOW_GET_HEADER,
            AbstractHttpResponse.EMPTY_BODY);
      default:
        // Keep the compiler happy
        throw new IllegalStateException("Someone forgot to add a case for the new enum value ?");
    }
  }
}
