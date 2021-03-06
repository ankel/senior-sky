package ankel.seniorsky.raptor.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author Binh Tran
 */
public class HttpServer
{
  public static final String SERVLET_NAME = "SeniorSky :: Raptor";
  public static final String URL_MATCH_ALL = "/*";
  public static final String ROOT_PATH = "/";

  private final Server server;
  private final ServerConnector serverConnector;
  private final ResourceConfig resourceConfig;

  private HttpServer(final ResourceConfig resourceConfig,
      final Server server, final ServerConnector serverConnector)
  {
    this.server = server;
    this.serverConnector = serverConnector;
    this.resourceConfig = resourceConfig;
  }

  private void configure(final Injector injector, final List<HttpServerFilter> filters)
  {
    final ContextHandlerCollection handlers = new ContextHandlerCollection();
    handlers.setServer(this.server);

    final ServletContextHandler servletContextHandler = new ServletContextHandler();
    servletContextHandler.getServletHandler().setEnsureDefaultServlet(false);
    servletContextHandler.setContextPath(ROOT_PATH);
    servletContextHandler.setServer(this.server);

    final ServletContainer servletContainer = new ServletContainer(resourceConfig);
    final ServletHolder servletHolder = new ServletHolder(servletContainer);
    servletHolder.setName(SERVLET_NAME);
    servletContextHandler.addServlet(servletHolder, URL_MATCH_ALL);

    filters.forEach((f) ->
    {
      final Filter filter = injector.getInstance(f.getFilterClass());
      final FilterHolder filterHolder = new FilterHolder(filter);
      servletContextHandler.addFilter(
          filterHolder,
          f.getPathSpec(),
          EnumSet.of(DispatcherType.REQUEST));
    });

    server.setHandler(servletContextHandler);
    server.addConnector(serverConnector);
  }

  public int getLocalPort()
  {
    return serverConnector.getLocalPort();
  }

  public String getHost()
  {
    return serverConnector.getHost();
  }

  public void start() throws Exception
  {
    server.start();
  }

  public void stop() throws Exception
  {
    if (server != null)
    {
      server.stop();
    }
  }

  public static HttpServerBuilder builder()
  {
    return new HttpServerBuilder();
  }

  public static final class HttpServerBuilder
  {
    private List<Module> modules = new ArrayList<>();
    private List<Class<?>> singletons = new ArrayList<>();
    private int port = 0;
    private List<HttpServerFilter> filters = new ArrayList<>();

    /**
     * Add {@link Module} from which to create injector
     */
    public HttpServerBuilder addModules(final Module... modules)
    {
      Collections.addAll(this.modules, modules);
      return this;
    }

    /**
     * Add class of JAX RS resource to be registered to the server.
     */
    public HttpServerBuilder addSingleton(final Class<?> klass)
    {
      this.singletons.add(klass);
      return this;
    }

    /**
     * Specify the port the server will be run on. Default value is 0 for auto select.
     */
    public HttpServerBuilder onPort(final int port)
    {
      this.port = port;
      return this;
    }

    public HttpServerFilter.HttpServerFilterBuilder addFilter()
    {
      return HttpServerFilter.builder(this);
    }

    public HttpServer build()
    {
      final Injector injector = Guice.createInjector(modules);
      final ResourceConfig resourceConfig = injector.getInstance(ResourceConfig.class);
      final Server server = injector.getInstance(Server.class);
      final ServerConnector serverConnector = injector.getInstance(ServerConnector.class);
      serverConnector.setPort(port);

      singletons.forEach((c) -> resourceConfig.register(injector.getInstance(c)));

      final HttpServer httpServer = new HttpServer(resourceConfig, server, serverConnector);
      httpServer.configure(injector, filters);
      return httpServer;
    }

    HttpServerBuilder addFilter(final HttpServerFilter httpServerFilter)
    {
      filters.add(httpServerFilter);
      return this;
    }
  }
}
