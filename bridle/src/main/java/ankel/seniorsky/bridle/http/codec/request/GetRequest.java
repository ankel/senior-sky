package ankel.seniorsky.bridle.http.codec.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Value;

import ankel.seniorsky.bridle.http.codec.response.AbstractHttpResponse;
import ankel.seniorsky.bridle.http.codec.visitor.HttpRequestVisitor;

/**
 * Represent a GET request
 *
 * @author Binh Tran
 */
@Value
public class GetRequest implements HttpRequest
{
  private final String uri;

  private final Map<String, List<String>> headers = new HashMap<>();

  @Override
  public AbstractHttpResponse accept(final HttpRequestVisitor visitor)
  {
    return visitor.visit(this);
  }

}
