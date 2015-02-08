package org.svomz.commons.application;

/**
 * Adds a default implementation for {@link AbstractApplication#run()}.
 *
 * A service application won't execute anything when {@link AbstractApplication#run()} is called. This
 * kind of application starts one or several services during its lifecycle which waits incoming requests.
 */
public class ServiceApplication extends AbstractApplication {

  @Override
  public void run() {

  }
}
