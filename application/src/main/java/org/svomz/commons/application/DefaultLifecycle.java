package org.svomz.commons.application;

import com.google.common.base.Preconditions;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import org.svomz.commons.core.Command;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages the lifecycle of an application allowing execution of commands
 * at startup (before the application runs) and before the stop.
 */
@ThreadSafe
public class DefaultLifecycle implements Lifecycle {
  private final Set<Command> startingCommand;
  private final Set<Command> stoppingCommands;
  private final Set<Command> runningCommands;
  private final Set<Command> terminatedCommands;

  private volatile Stage stage;

  @Inject
  protected DefaultLifecycle(@StartingCommand Set<Command> startingCommands,
      @RunningCommand Set<Command> runningCommands, @StoppingCommand Set<Command> stoppingCommands,
      @TerminatedCommand Set<Command> terminatedCommands) {
    Preconditions.checkNotNull(startingCommands);
    Preconditions.checkNotNull(runningCommands);
    Preconditions.checkNotNull(stoppingCommands);
    Preconditions.checkNotNull(terminatedCommands);

    this.startingCommand = Collections.unmodifiableSet(startingCommands);
    this.runningCommands = Collections.unmodifiableSet(runningCommands);
    this.stoppingCommands = Collections.unmodifiableSet(stoppingCommands);
    this.terminatedCommands = Collections.unmodifiableSet(terminatedCommands);
    this.stage = Stage.NEW;
  }

  private DefaultLifecycle(Builder builder) {
    this(builder.startingCommands, builder.runningCommands, builder.stoppingCommands, builder.terminatedCommands);
  }

  /**
   * Executes the startup commands and flags the application as running.
   *
   * If the commands launch services in other threads the lifecycle stage may pass from STARTING to
   * RUNNING even if the service is not fully launched yet.
   *
   * This method guaranteed that every commands configured to be called has be called.
   *
   * If commands launch other services in new threads this methods does not guaranteed that these
   * services are launched when the stage is changed to RUNNING.
   */
  @Override
  public synchronized void start() {
    Preconditions.checkState(this.stage == Stage.NEW,
        "The lifecycle can only be started once.");
    this.stage = Stage.STARTING;

    for (Command command : this.startingCommand) {
      command.run();
    }

    this.stage = Stage.RUNNING;

    for (Command command : this.runningCommands) {
      command.run();
    }
  }

  /**
   * Executes the stop commands and flags the application as not running.
   */
  @Override
  public synchronized void stop() {
    Preconditions.checkState(this.stage
        == Stage.RUNNING, "The lifecycle can only be stopped once while the application is running.");
    this.stage = Stage.STOPPING;

    for (Command command : this.stoppingCommands) {
      command.run();
    }

    this.stage = Stage.TERMINATED;

    for (Command command : this.terminatedCommands) {
      command.run();
    }
  }

  /**
   * Shutdown the application without calling stop methods.
   */
  @Override
  public void abort() {
    System.exit(1);
  }

  /**
   * Wait
   */
  @Override
  public void awaitRunning() {
    Preconditions.checkState(this.stage == Stage.RUNNING, "The lifecycle is not running.");

    while (this.isRunning()) { /* do nothing, just wait */}
  }

  public boolean isRunning() {
    return this.stage == Stage.RUNNING;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static enum Stage {
    NEW,
    STARTING,
    RUNNING,
    STOPPING,
    TERMINATED
  }

  /**
   * Builder for lifecycle
   */
  public static class Builder {

    private final Set<Command> startingCommands;
    private final Set<Command> runningCommands;
    private final Set<Command> stoppingCommands;
    private final Set<Command> terminatedCommands;

    public Builder() {
      this.startingCommands = new HashSet<>();
      this.runningCommands = new HashSet<>();
      this.stoppingCommands = new HashSet<>();
      this.terminatedCommands = new HashSet<>();
    }

    public Builder addStartingCommands(Command... commands) {
      Preconditions.checkNotNull(commands);
      this.startingCommands.addAll(Arrays.asList(commands));
      return this;
    }

    public Builder addRunningCommands(Command... commands) {
      Preconditions.checkNotNull(commands);
      this.runningCommands.addAll(Arrays.asList(commands));
      return this;
    }

    public Builder addStoppingCommands(Command... commands) {
      Preconditions.checkNotNull(commands);
      this.stoppingCommands.addAll(Arrays.asList(commands));
      return this;
    }

    public Builder addTerminatedCommands(Command... commands) {
      Preconditions.checkNotNull(commands);
      this.terminatedCommands.addAll(Arrays.asList(commands));
      return this;
    }

    public DefaultLifecycle build() {
      return new DefaultLifecycle(this);
    }

  }


  /**
   * Used for documentation purposes to indicate that an instance of a class is called before the application starts.
   */
  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.PARAMETER})
  public @interface StartingCommand { }

  /**
   * Used for documentation purposes to indicate that an instance of a class is called when the application is running.
   */
  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.PARAMETER})
  public @interface RunningCommand { }


  /**
   * Used for documentation purposes to indicate that an instance of a class is called before the application stops.
   */
  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.PARAMETER})
  public @interface StoppingCommand { }

  /**
   * Used for documentation purposes to indicate that an instance of a class is called when the application is stopped.
   */
  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.PARAMETER})
  public @interface TerminatedCommand { }
}
