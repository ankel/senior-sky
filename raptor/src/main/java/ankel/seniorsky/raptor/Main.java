package ankel.seniorsky.raptor;

import lombok.Getter;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import ankel.seniorsky.raptor.rest.SimpleResource;

/**
 * @author Binh Tran
 */
public class Main
{
  public static final int MIN_THREAD = 2;
  public static final int MAX_THREAD = 20;
  public static final String SERVLET_NAME = "SeniorSky :: Raptor";
  public static final String URL_MATCH_ALL = "/*";

  @Getter
  private static Server server;

  public static void main(final String args[]) throws Exception
  {
    final ThreadPool pool = new QueuedThreadPool(MAX_THREAD, MIN_THREAD);

    server = new Server(pool);

    final ContextHandlerCollection handlers = new ContextHandlerCollection();
    handlers.setServer(server);

    final ServletContextHandler servletContextHandler = new ServletContextHandler();
    servletContextHandler.getServletHandler().setEnsureDefaultServlet(false);
    servletContextHandler.setContextPath("/");
    servletContextHandler.setServer(server);

    final ResourceConfig resourceConfig = buildResourceConfig();
    final ServletContainer servletContainer = new ServletContainer(resourceConfig);
    final ServletHolder servletHolder = new ServletHolder(servletContainer);
    servletHolder.setName(SERVLET_NAME);
    servletContextHandler.addServlet(servletHolder, URL_MATCH_ALL);

    ServerConnector http = new ServerConnector(server);
    http.setHost("localhost");
    http.setPort(8080);
    http.setIdleTimeout(30000);

    server.setHandler(servletContextHandler);
    server.addConnector(http);
    server.start();
    server.join();
  }

  private static ResourceConfig buildResourceConfig()
  {
    final ResourceConfig resourceConfig = new ResourceConfig();

    // Disable built in json processing
    resourceConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
    resourceConfig.property(CommonProperties.JSON_PROCESSING_FEATURE_DISABLE, true);
    resourceConfig.property(CommonProperties.MOXY_JSON_FEATURE_DISABLE, true);

    resourceConfig.register(SimpleResource.class);
    // Use jackson for json serialization
    resourceConfig.register(new JacksonJsonProvider(new ObjectMapper()));

    return resourceConfig;
  }

}
