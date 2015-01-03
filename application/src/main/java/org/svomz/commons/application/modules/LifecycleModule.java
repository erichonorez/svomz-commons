package org.svomz.commons.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import org.svomz.commons.application.DefaultLifecycle;
import org.svomz.commons.application.Lifecycle;
import org.svomz.commons.core.Command;


public class LifecycleModule extends AbstractModule {
  @Override
  protected void configure() {
    this.bind(Lifecycle.class).to(DefaultLifecycle.class).in(Singleton.class);
    // Add default binding to empty sets.
    LifecycleModule.startingCommandBinder(binder());
    LifecycleModule.runningCommandBinder(binder());
    LifecycleModule.stoppingCommandBinder(binder());
    LifecycleModule.terminatedCommandBinder(binder());
  }

  public static void addStartingCommand(final Binder binder, final Command command) {
    LifecycleModule.startingCommandBinder(binder).addBinding().toInstance(command);
  }

  public static void addStartingCommand(final Binder binder, final Class<? extends Command> command) {
    LifecycleModule.startingCommandBinder(binder).addBinding().to(command);
  }

  public static void addStoppingCommand(final Binder binder, final Command command) {
    LifecycleModule.stoppingCommandBinder(binder).addBinding().toInstance(command);
  }

  public static void addStoppingCommand(final Binder binder, final Class<? extends Command> command) {
    LifecycleModule.stoppingCommandBinder(binder).addBinding().to(command);
  }

  public static void addRunningCommand(final Binder binder, final Command command) {
    LifecycleModule.runningCommandBinder(binder).addBinding().toInstance(command);
  }

  public static void addRunningCommand(final Binder binder, final Class<? extends Command> command) {
    LifecycleModule.runningCommandBinder(binder).addBinding().to(command);
  }

  public static void addTerminatedCommand(final Binder binder, final Command command) {
    LifecycleModule.terminatedCommandBinder(binder).addBinding().toInstance(command);
  }

  public static void addTerminatedCommand(final Binder binder, final Class<? extends Command> command) {
    LifecycleModule.terminatedCommandBinder(binder).addBinding().to(command);
  }

  private static Multibinder<Command> startingCommandBinder(final Binder binder) {
    return Multibinder.newSetBinder(binder, Command.class, DefaultLifecycle.StartingCommand.class);
  }

  private static Multibinder<Command> stoppingCommandBinder(final Binder binder) {
    return Multibinder.newSetBinder(binder, Command.class, DefaultLifecycle.StoppingCommand.class);
  }

  private static Multibinder<Command> runningCommandBinder(final Binder binder) {
    return Multibinder.newSetBinder(binder, Command.class, DefaultLifecycle.RunningCommand.class);
  }

  private static Multibinder<Command> terminatedCommandBinder(final Binder binder) {
    return Multibinder.newSetBinder(binder, Command.class, DefaultLifecycle.TerminatedCommand.class);
  }
}
