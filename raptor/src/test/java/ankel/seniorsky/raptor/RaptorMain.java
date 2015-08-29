package ankel.seniorsky.raptor;

import ankel.seniorsky.raptor.config.RaptorModule;
import ankel.seniorsky.raptor.rest.SimpleResource;
import ankel.seniorsky.raptor.server.HttpServer;

/**
 * @author Binh Tran
 */
public class RaptorMain
{
  public static void main(String[] args) throws Exception
  {
    final HttpServer httpServer = HttpServer.builder()
        .addModules(new RaptorModule())
        .addSingleton(SimpleResource.class)
        .withPort(8080)
        .build();

    httpServer.start();

    httpServer.join();
  }
}
