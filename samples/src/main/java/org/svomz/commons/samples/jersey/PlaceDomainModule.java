package org.svomz.commons.samples.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import org.svomz.commons.samples.jersey.domain.InMemoryPlaceRepository;
import org.svomz.commons.samples.jersey.domain.PlaceRepository;

public class PlaceDomainModule extends AbstractModule {

  @Override
  protected void configure() {
    this.bind(PlaceRepository.class).to(InMemoryPlaceRepository.class).in(Singleton.class);
  }

}
