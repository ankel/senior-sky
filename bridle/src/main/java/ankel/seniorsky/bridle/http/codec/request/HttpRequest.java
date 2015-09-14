package ankel.seniorsky.bridle.http.codec.request;

import ankel.seniorsky.bridle.http.codec.response.AbstractHttpResponse;
import ankel.seniorsky.bridle.http.codec.visitor.HttpRequestVisitor;

/**
 * Base interface for an HTTP request
 *
 * @author Binh Tran
 */
public interface HttpRequest
{
  AbstractHttpResponse accept(final HttpRequestVisitor visitor);
}
