package org.svomz.commons.samples.placesapi.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link org.svomz.commons.samples.placesapi.domain.PlaceRepository} with an in memory
 * storage system.
 *
 * This implementation is voluntary simple.
 */
public class InMemoryPlaceRepository implements PlaceRepository {

  private final Set<Place> places;

  public InMemoryPlaceRepository() {
    this.places = new HashSet<>();
  }

  @Override
  public Place save(Place place) {
    this.places.add(place);
    return place;
  }

  @Override
  public Set<Place> getAll() {
    return Collections.unmodifiableSet(this.places);
  }
}
