package org.svomz.commons.samples.jersey;

import org.svomz.commons.samples.jersey.domain.Place;
import org.svomz.commons.samples.jersey.domain.PlaceRepository;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Places end point that allow api users to save new places and list them.
 *
 * This end point is voluntary simple.
 */
@Path("/places")
public class PlaceResource {

  private final PlaceRepository placeRepository;

  @Inject
  public PlaceResource(PlaceRepository placeRepository) {
    this.placeRepository = placeRepository;
  }

  @GET
  @Produces("application/json")
  public Set<Place> getPlaces() {
    return this.placeRepository.getAll();
  }

  @POST
  @Consumes("application/json")
  @Produces("application/json")
  public Place addPlace(final Place place) {
    return this.placeRepository.save(place);
  }

}
