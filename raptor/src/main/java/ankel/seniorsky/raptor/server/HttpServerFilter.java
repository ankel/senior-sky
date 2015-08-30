package ankel.seniorsky.raptor.server;

import javax.servlet.Filter;

import lombok.Value;

/**
 * @author Binh Tran
 */
@Value
public class HttpServerFilter
{
  String pathSpec;
  Class<? extends Filter> filterClass;

  static HttpServerFilterBuilder builder(final HttpServer.HttpServerBuilder httpServerBuilder)
  {
    return new HttpServerFilterBuilder(httpServerBuilder);
  }

  public static class HttpServerFilterBuilder
  {
    private String pathSpec;
    private Class<? extends Filter> filterClass;
    private final HttpServer.HttpServerBuilder httpServerBuilder;

    private HttpServerFilterBuilder(final HttpServer.HttpServerBuilder httpServerBuilder)
    {
      this.httpServerBuilder = httpServerBuilder;
    }

    public HttpServerFilterBuilder onPath(final String pathSpec)
    {
      this.pathSpec = pathSpec;
      return this;
    }

    public HttpServerFilterBuilder ofType(final Class<? extends Filter> filterClass)
    {
      this.filterClass = filterClass;
      return this;
    }

    public HttpServer.HttpServerBuilder build()
    {
      return httpServerBuilder.addFilter(new HttpServerFilter(pathSpec, filterClass));
    }

  }
}
