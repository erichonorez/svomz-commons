package org.svomz.commons.samples.jersey.domain;

import java.util.Set;

/**
 * Defines a set of operation to store and retrieve place in a storage system.
 */
public interface PlaceRepository {

  /**
   * Insert or update the place in the storage system.
   */
  public Place save(final Place place);

  /**
   * Get the list of persited places
   */
  public Set<Place> getAll();

}
