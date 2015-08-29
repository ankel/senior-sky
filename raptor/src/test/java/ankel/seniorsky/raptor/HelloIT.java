package ankel.seniorsky.raptor;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import lombok.Cleanup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
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
  private ObjectMapper mapper = new ObjectMapper();

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

  @Test
  public void defaultLightTest() throws Exception
  {
    URL url = new URL(
        String.format(
            "http://%s:%d%s%s",
            httpServer.getHost(),
            httpServer.getLocalPort(),
            SimpleResource.RESOURCE_PATH,
            SimpleResource.LIGHT_PATH));
    @Cleanup
    InputStream inputStream = url.openStream();
    SimpleResource.LightResponse lightResponse = mapper.readValue(inputStream,
        SimpleResource.LightResponse.class);
    assertFalse((boolean) lightResponse.getResults().get(SimpleResource.LightResponse.LIGHT_STATE));

    URLConnection connection = url.openConnection();
    connection.setDoOutput(true);
    connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

    @Cleanup
    OutputStream outputStream = connection.getOutputStream();
    outputStream.write("true".getBytes());

    inputStream = connection.getInputStream();
    lightResponse = mapper.readValue(inputStream,
        SimpleResource.LightResponse.class);
    assertTrue((boolean) lightResponse.getResults().get(SimpleResource.LightResponse.LIGHT_STATE));
  }
}
