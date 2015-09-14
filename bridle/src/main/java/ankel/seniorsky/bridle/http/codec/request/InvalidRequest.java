package ankel.seniorsky.bridle.http.codec.request;

import lombok.Value;

import ankel.seniorsky.bridle.http.codec.response.AbstractHttpResponse;
import ankel.seniorsky.bridle.http.codec.visitor.HttpRequestVisitor;

/**
 * Represent an Invalid Request
 *
 * @author Binh Tran
 */
@Value
public class InvalidRequest implements HttpRequest
{

  private final Reason reason;

  @Override
  public AbstractHttpResponse accept(final HttpRequestVisitor visitor)
  {
    return visitor.visit(this);
  }

  public enum Reason
  {
    BAD_REQUEST_LINE,
    NOT_RELATIVE_URI,
    METHOD_NOT_ALLOWED
  }
}
