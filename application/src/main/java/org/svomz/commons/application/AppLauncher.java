package org.svomz.commons.application;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.svomz.commons.application.modules.LifecycleModule;

/**
 * An application launcher that sets up a framework for pluggable binding modules. This class should be called
 * directly as the main class.
 */
public class AppLauncher {

  @Inject
  private Lifecycle lifecycle;

  private AppLauncher() {
  }

  private void run(Application application) {
    this.configureInjections(application);

    lifecycle.start();
    application.run();
    lifecycle.awaitRunning();
  }

  private void configureInjections(Application application) {
    Iterable<Module> modules = ImmutableList.<Module>builder()
        .add(new LifecycleModule())
        .addAll(application.getModules())
        .build();

    Injector injector = Guice.createInjector(modules);
    injector.injectMembers(application);
    injector.injectMembers(this);
  }

  public static void launch(Class<? extends Application> applicationClass) {
    Preconditions.checkNotNull(applicationClass);

    try {
      new AppLauncher().run(applicationClass.newInstance());
    } catch (IllegalAccessException | InstantiationException ex) {
      throw new IllegalStateException(ex);
    }
  }

}
