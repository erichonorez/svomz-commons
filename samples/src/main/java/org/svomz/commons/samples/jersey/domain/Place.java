package org.svomz.commons.samples.jersey.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a place by a name and its GPS Coordinates.
 *
 * This implementation is voluntarily simple. A place is uniquely identified by its name.
 */
public class Place {

  private final String name;
  private final double longitude;
  private final double latitude;

  @JsonCreator
  public Place(@JsonProperty("name") final String name, @JsonProperty("longitude") final double longitude,
               @JsonProperty("latitude") final double latitude) {
    this.name = name;
    this.longitude = longitude;
    this.latitude = latitude;
  }

  public String getName() {
    return name;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Place place = (Place) o;

    if (!name.equals(place.name)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
