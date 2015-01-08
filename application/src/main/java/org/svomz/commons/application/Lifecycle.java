package org.svomz.commons.application;

import com.google.common.base.Preconditions;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;

import org.svomz.commons.core.Command;

import javax.annotation.concurrent.ThreadSafe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Application lifecycle running predefined commands at each stages.
 *
 * It could also be used by thread to wait until the application shutdowns.
 */
@ThreadSafe
public final class Lifecycle {

  private static final Logger LOG = Logger.getLogger(Lifecycle.class.getName());

  private final Set<Command> startingCommand;
  private final Set<Command> stoppingCommands;
  private final Set<Command> runningCommands;
  private final Set<Command> terminatedCommands;

  private volatile Stage stage;

  /**
   * Constructs a lifecycle with a set of commands to run at the different {@link org.svomz.commons.application.Lifecycle.Stage}
   *
   * @param startingCommands {@link org.svomz.commons.core.Command} to run when the application is starting.
   * @param runningCommands {@link org.svomz.commons.core.Command} to run when the application is running.
   * @param stoppingCommands {@link org.svomz.commons.core.Command} to run when the application is stopping.
   * @param terminatedCommands {@link org.svomz.commons.core.Command} to run when the application is stopped.
   */
  @Inject
  protected Lifecycle(@StartingCommand final Set<Command> startingCommands,
    @RunningCommand final Set<Command> runningCommands,
    @StoppingCommand final Set<Command> stoppingCommands,
    @TerminatedCommand final Set<Command> terminatedCommands) {
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

  private Lifecycle(Builder builder) {
    this(builder.startingCommands, builder.runningCommands, builder.stoppingCommands,
      builder.terminatedCommands);
  }

  /**
   * Executes sequentially the starting commands. The lifecycle's stage should be {@link org.svomz.commons.application.Lifecycle.Stage#RUNNING}
   * when all startup commands has been properly executed. Once the service is running it
   * executes sequentially commands configured for the {@link org.svomz.commons.application.Lifecycle.Stage#RUNNING} stage.
   *
   * There is no catch of exception thrown by a starting command. It means that the lifecycle won't
   * never reach the {@link org.svomz.commons.application.Lifecycle.Stage#RUNNING} if something goes
   * wrong in one of these command.
   *
   * However an exception thrown by a running command won't prevent commands that follow to be executed.
   *
   * The lifecycle can only be started once.
   */
  public synchronized void start() {
    Preconditions.checkState(this.stage == Stage.NEW,
      "The lifecycle can only be started once.");
    this.stage = Stage.STARTING;

    for (Command command : this.startingCommand) {
      command.run();
    }

    this.stage = Stage.RUNNING;

    for (Command command : this.runningCommands) {
      try {
        command.run();
      } catch (Exception ex) {
        LOG.warning(ex.getMessage());
      }
    }
  }

  /**
   * Executes sequentially the stopping commands. The lifecycle's stage should be {@link org.svomz.commons.application.Lifecycle.Stage#STOPPING}
   * when all startup commands has been properly executed. Once the service is terminated it
   * executes sequentially commands configured for the {@link org.svomz.commons.application.Lifecycle.Stage#TERMINATED} stage.
   *
   * Any exception thrown by a stopping or terminated commands won't prevent commands that follow to
   * be executed.
   *
   * The lifecycle can only be stopped once.
   */
  public synchronized void stop() {
    Preconditions.checkState(this.stage == Stage.RUNNING,
      "The lifecycle can only be stopped once while the application is running.");
    this.stage = Stage.STOPPING;

    for (Command command : this.stoppingCommands) {
      try {
        command.run();
      } catch (Exception ex) {
        LOG.warning(ex.getMessage());
      }
    }

    this.stage = Stage.TERMINATED;

    for (Command command : this.terminatedCommands) {
      try {
        command.run();
      } catch (Exception ex) {
        LOG.warning(ex.getMessage());
      }
    }
  }

  /**
   * Kills the application directly.
   */
  public void abort() {
    System.exit(1);
  }

  /**
   * Used by other thread to wait as long as the lifecycle is running. This methods could only be
   * called if the lifecycle is running.
   */
  public void awaitRunning() {
    Preconditions.checkState(this.stage == Stage.RUNNING, "The lifecycle is not running.");

    while (this.isRunning()) {
      /* do nothing, just wait */
    }
  }

  public boolean isRunning() {
    return this.stage == Stage.RUNNING;
  }

  public Stage getStage() {
    return this.stage;
  }

  /**
   * @return a lifecycle builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Stages of a {@link org.svomz.commons.application.Lifecycle}
   */
  public static enum Stage {
    NEW,
    STARTING,
    RUNNING,
    STOPPING,
    TERMINATED
  }

  /**
   * Builds a {@link org.svomz.commons.application.Lifecycle}.
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

    public Builder addStartingCommands(final Command... commands) {
      Preconditions.checkNotNull(commands);
      this.startingCommands.addAll(Arrays.asList(commands));
      return this;
    }

    public Builder addRunningCommands(final Command... commands) {
      Preconditions.checkNotNull(commands);
      this.runningCommands.addAll(Arrays.asList(commands));
      return this;
    }

    public Builder addStoppingCommands(final Command... commands) {
      Preconditions.checkNotNull(commands);
      this.stoppingCommands.addAll(Arrays.asList(commands));
      return this;
    }

    public Builder addTerminatedCommands(final Command... commands) {
      Preconditions.checkNotNull(commands);
      this.terminatedCommands.addAll(Arrays.asList(commands));
      return this;
    }

    public Lifecycle build() {
      return new Lifecycle(this);
    }

  }


  /**
   * Used for documentation purposes to indicate that an instance of a class is called before the
   * application starts.
   */
  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.PARAMETER})
  public @interface StartingCommand {

  }

  /**
   * Used for documentation purposes to indicate that an instance of a class is called when the
   * application is running.
   */
  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.PARAMETER})
  public @interface RunningCommand {

  }


  /**
   * Used for documentation purposes to indicate that an instance of a class is called before the
   * application stops.
   */
  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.PARAMETER})
  public @interface StoppingCommand {

  }

  /**
   * Used for documentation purposes to indicate that an instance of a class is called when the
   * application is stopped.
   */
  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.PARAMETER})
  public @interface TerminatedCommand {

  }
}
