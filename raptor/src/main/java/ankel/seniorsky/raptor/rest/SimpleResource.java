package ankel.seniorsky.raptor.rest;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import com.google.common.collect.ImmutableMap;

/**
 * @author Binh Tran
 */
@Path(SimpleResource.RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimpleResource
{
  public static final String RESOURCE_PATH = "/";
  public static final String LIGHT_PATH = "light";
  public static final ImmutableMap<String, String> lightOperations = ImmutableMap.of(
      "GET", RESOURCE_PATH + LIGHT_PATH,
      "POST", RESOURCE_PATH + LIGHT_PATH);

  private final AtomicBoolean lightMode = new AtomicBoolean(false);

  @GET
  public String hello()
  {
    return "Hello, world!";
  }

  @POST
  @Path("light")
  public Response switchLight(final boolean lightSwitch)
  {
    lightMode.set(lightSwitch);

    // Possible race condition here.
    return Response.accepted()
        .entity(
            LightResponse
                .builder()
                .result(LightResponse.LIGHT_STATE, lightSwitch)
                .build())
        .build();
  }

  @GET
  @Path("light")
  public LightResponse getLightMode()
  {
    return LightResponse.builder()
        .result(LightResponse.LIGHT_STATE, lightMode.get())
        .build();
  }

  @AllArgsConstructor
  @Getter
  @Setter
  public static class SimpleResponse
  {
    Map<String, String> operations;
    Map<String, Object> results;
  }

  public static final class LightResponse extends SimpleResponse
      // See what happens when I get my hand on a thesaurus?
  {
    public static final String LIGHT_STATE = "state";

    public LightResponse()
    {
      super(null, null);
    }

    @Builder
    public LightResponse(@Singular final Map<String, Object> results)
    {
      super(lightOperations, results);
    }
  }

}
