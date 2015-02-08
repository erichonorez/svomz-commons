package org.svomz.commons.samples.clidispatcher;

import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

import org.svomz.commons.application.modules.AbstractApplicationModule;

/**
 * Configures the {@link CliDispatcher} modules by binding the
 * CliDispatcher and by adding {@link CliDispatcherLauncher} and
 * {@link org.svomz.commons.samples.cli .CliDispatcherKiller} to lifecycle.
 *
 * It allows you to configure the {@link CliDispatcher} by adding
 * endpoints.
 */
public class CliDispatcherModule extends AbstractApplicationModule {

  @Override
  protected void configure() {
    this.bind(CliDispatcher.class).in(Singleton.class);
    //Bind empty set of endpoints
    CliDispatcherModule.endpointBinder(binder());
    this.onStarting(CliDispatcherLauncher.class);
    this.onStopping(CliDispatcherKiller.class);
  }

  /**
   * Adds an the given endpoint instance to the set of CliDispatcher configured endpoints.
   */
  public static void addEndPoint(final Binder binder, CliEndPoint endPoint) {
    CliDispatcherModule.endpointBinder(binder).addBinding().toInstance(endPoint);
  }

  /**
   * Adds the given endpoint class to the set of CliDispatcher configured endpoints.
   */
  public static void addEndPoint(final Binder binder, Class<? extends CliEndPoint> endPoint) {
    CliDispatcherModule.endpointBinder(binder).addBinding().to(endPoint).in(Singleton.class);
  }

  private static Multibinder<CliEndPoint> endpointBinder(final Binder binder) {
    return Multibinder.newSetBinder(binder, CliEndPoint.class);
  }
}
