package ankel.seniorsky.raptor;

import ankel.seniorsky.raptor.config.RaptorModule;
import ankel.seniorsky.raptor.rest.CORSFilter;
import ankel.seniorsky.raptor.rest.SimpleResource;
import ankel.seniorsky.raptor.server.HttpServer;

/**
 * @author Binh Tran
 */
public class RaptorMain
{
  public static void main(String[] args) throws Exception
  {
    // @formatter:off
    final HttpServer httpServer = HttpServer.builder()
        .addModules(new RaptorModule())
        .onPort(8080)
        .addFilter()
          .onPath("/*")
          .ofType(CORSFilter.class)
          .build()
        .addSingleton(SimpleResource.class)
        .build();
    // @formatter:on

    httpServer.start();

    System.out.println("Press ENTER to exit");
    System.in.read();

    httpServer.stop();
  }
}
