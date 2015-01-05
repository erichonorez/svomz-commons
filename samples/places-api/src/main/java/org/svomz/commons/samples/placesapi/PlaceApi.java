package org.svomz.commons.samples.placesapi;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import org.svomz.commons.application.AbstractApplication;
import org.svomz.commons.application.AppLauncher;
import org.svomz.commons.application.modules.HttpServerModule;
import org.svomz.commons.application.modules.JerseyModule;

import java.util.ArrayList;
import java.util.List;

/**
 * This example show you how you can build Restful web services with the svomz application framework.
 *
 * Our REST application is pretty simple but it shows:
 * <ol>
 *   <li>how you can declare dependencies to other modules like {@link org.svomz.commons.application.modules.HttpServerModule}
 *   and {@link org.svomz.commons.application.modules.JerseyModule};</li>
 *   <li>how you can configure jersey to serve your resources;</li>
 *   <li>how you can configure Jersey to be able to inject you application service into your resources.</li>
 * </ol>
 *
 * This class is the entry point from which we will configure required modules.
 *
 * This class contains the main method which allow you to start your application on command line with:
 * <code>
 *   java -jar RestApp
 * </code>
 */
public class PlaceApi extends AbstractApplication {

  @Override
  public void run() {
    // Does nothing. The application logic is in the HealthResource rest end point.
  }

  @Override
  public Iterable<? extends Module> getModules() {
    List<Module> modules = new ArrayList<>();
    modules.add(new HttpServerModule());
    modules.add(new JerseyModule(PlaceResourceConfig.class));
    modules.add(new PlaceDomainModule());
    return modules;
  }

  public static void main(String[] args) {
    AppLauncher.launch(PlaceApi.class);
  }
}
