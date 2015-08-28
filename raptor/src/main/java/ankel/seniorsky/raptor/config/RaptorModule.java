package ankel.seniorsky.raptor.config;

import javax.inject.Singleton;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import ankel.seniorsky.raptor.rest.SimpleResource;

/**
 * Configure Raptor
 *
 * @author Binh Tran
 */
public class RaptorModule extends AbstractModule
{
  public static final int MIN_THREAD = 2;
  public static final int MAX_THREAD = 20;

  @Override
  protected void configure()
  {
    // no op
  }

  @Provides
  @Singleton
  public ResourceConfig providesResourceConfig()
  {
    final ResourceConfig resourceConfig = new ResourceConfig();

    // Disable built in json processing
    resourceConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
    resourceConfig.property(CommonProperties.JSON_PROCESSING_FEATURE_DISABLE, true);
    resourceConfig.property(CommonProperties.MOXY_JSON_FEATURE_DISABLE, true);

    // Use jackson for json serialization
    resourceConfig.register(new JacksonJsonProvider(new ObjectMapper()));

    return resourceConfig;
  }

  @Provides
  @Singleton
  public SimpleResource providesSimpleResource()
  {
    return new SimpleResource();
  }

  @Provides
  @Singleton
  public ThreadPool providesThreadPool()
  {
    return new QueuedThreadPool(MAX_THREAD, MIN_THREAD);
  }

  @Provides
  @Singleton
  public Server providesServer(final ThreadPool threadPool)
  {
    return new Server(threadPool);
  }

  @Provides
  @Singleton
  public ServerConnector providesServerConnector(final Server server)
  {
    final ServerConnector serverConnector = new ServerConnector(server);
    serverConnector.setHost("localhost");
    // Keep alive
    // serverConnector.setIdleTimeout(TimeUnit.SECONDS.toMillis(115));
    return serverConnector;
  }
}
