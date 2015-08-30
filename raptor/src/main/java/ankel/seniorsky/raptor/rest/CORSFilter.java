package ankel.seniorsky.raptor.rest;

import static com.google.common.net.HttpHeaders.*;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Very basic CORS filter, and definitely not secure.
 *
 * @author Binh Tran
 */
public class CORSFilter implements Filter
{
  @Override
  public void init(final FilterConfig filterConfig) throws ServletException
  {
    // no op
  }

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response,
      final FilterChain chain)
      throws IOException, ServletException
  {
    if (response instanceof HttpServletResponse)
    {
      final HttpServletResponse httpServletResponse = (HttpServletResponse) response;

      httpServletResponse.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      httpServletResponse.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "*");
      httpServletResponse.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "X-Requested-With, Content-Type");
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy()
  {
    // no op
  }
}
