package org.svomz.commons.samples.jersey;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.svomz.commons.application.modules.JerseyModule;

/**
 * Configures Jersey by registering resource classes and features.
 */
public class PlaceResourceConfig extends ResourceConfig {

  public PlaceResourceConfig() {
    this.register(PlaceResource.class);
    this.register(JacksonFeature.class);
    /*
     * We want to be able to inject Guice services configured in the PlaceApplicationModule into our
     * rest resources.
     */
    this.register(JerseyModule.AppInjector.class);
  }

}
