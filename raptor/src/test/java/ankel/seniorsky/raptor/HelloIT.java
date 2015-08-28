package ankel.seniorsky.raptor;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import lombok.Cleanup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.CharStreams;

import ankel.seniorsky.raptor.config.RaptorModule;
import ankel.seniorsky.raptor.rest.SimpleResource;
import ankel.seniorsky.raptor.server.HttpServer;

/**
 * @author Binh Tran
 */
public class HelloIT
{

  private HttpServer httpServer;

  @Before
  public void setUp() throws Exception
  {
    httpServer = HttpServer.builder()
        .addModules(new RaptorModule())
        .addSingleton(SimpleResource.class)
        .build();

    httpServer.start();
  }

  @After
  public void tearDown() throws Exception
  {
    httpServer.stop();
  }

  @Test
  public void helloWorldTest() throws Exception
  {
    URL url = new URL(
        String.format(
            "http://%s:%d", httpServer.getHost(), httpServer.getLocalPort()));
    @Cleanup
    InputStream inputStream = url.openStream();
    assertEquals("Hello, world!", CharStreams.toString(new InputStreamReader(inputStream)));
  }
}
