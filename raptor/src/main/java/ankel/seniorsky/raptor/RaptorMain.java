package ankel.seniorsky.raptor;

import lombok.Getter;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

/**
 * @author Binh Tran
 */
public class RaptorMain
{

  @Getter
  private static Server server;

  @Getter
  private static ServerConnector serverConnector;

  public static void main(final String args[])
  {
    System.out.println("Good!");
  }

}
