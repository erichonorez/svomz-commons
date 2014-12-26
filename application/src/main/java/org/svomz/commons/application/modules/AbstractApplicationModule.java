package org.svomz.commons.application.modules;

import com.google.inject.AbstractModule;
import org.svomz.commons.core.Command;

public abstract class AbstractApplicationModule extends AbstractModule {

  public AbstractApplicationModule onStarting(final Command command) {
    LifecycleModule.addStartingCommand(binder(), command);
    return this;
  }

  public AbstractApplicationModule onStarting(final Class<? extends Command> command) {
    LifecycleModule.addStartingCommand(binder(), command);
    return this;
  }

  public AbstractApplicationModule onRunning(final Command command) {
    LifecycleModule.addRunningCommand(binder(), command);
    return this;
  }

  public AbstractApplicationModule onRunning(final Class<? extends Command> command) {
    LifecycleModule.addRunningCommand(binder(), command);
    return this;
  }

  public AbstractApplicationModule onStopping(final Command command) {
    LifecycleModule.addStoppingCommand(binder(), command);
    return this;
  }

  public AbstractApplicationModule onStopping(final Class<? extends Command> command) {
    LifecycleModule.addStoppingCommand(binder(), command);
    return this;
  }

  public AbstractApplicationModule onTerminated(final Command command) {
    LifecycleModule.addTerminatedCommand(binder(), command);
    return this;
  }

  public AbstractApplicationModule onTerminated(final Class<? extends Command> command) {
    LifecycleModule.addTerminatedCommand(binder(), command);
    return this;
  }

}
