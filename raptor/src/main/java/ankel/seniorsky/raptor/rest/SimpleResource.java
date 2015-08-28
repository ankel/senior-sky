package ankel.seniorsky.raptor.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Binh Tran
 */
@Path("/")
public class SimpleResource
{
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String hello()
  {
    return "Hello, world!";
  }
}
