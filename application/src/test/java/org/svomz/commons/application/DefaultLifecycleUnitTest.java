package org.svomz.commons.application;

import org.junit.Assert;
import org.junit.Test;
import org.svomz.commons.core.Command;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DefaultLifecycleUnitTest {

  @Test
  public void testStartupCommands() {
    Command startupCommand1 = mock(Command.class);
    Command startupCommand2 = mock(Command.class);

    DefaultLifecycle lifecycle = DefaultLifecycle.builder()
        .addStartingCommands(startupCommand1, startupCommand2)
        .build();
    lifecycle.start();

    Assert.assertTrue(lifecycle.isRunning());
    verify(startupCommand1).run();
    verify(startupCommand2).run();
  }

  @Test
  public void testShutdownCommands() {
    Command shutdownCommand1 = mock(Command.class);
    Command shutdownCommand2 = mock(Command.class);

    DefaultLifecycle lifecycle = DefaultLifecycle.builder()
        .addStoppingCommands(shutdownCommand1, shutdownCommand2)
        .build();
    lifecycle.start();

    Assert.assertTrue(lifecycle.isRunning());
    lifecycle.stop();
    verify(shutdownCommand1).run();
    verify(shutdownCommand2).run();
    Assert.assertFalse(lifecycle.isRunning());
  }

  @Test
  public void testRunningCommands() {
    Command runningCommand1 = mock(Command.class);
    DefaultLifecycle
        lifecycle = DefaultLifecycle.builder().addRunningCommands(runningCommand1).build();

    lifecycle.start();
    Assert.assertTrue(lifecycle.isRunning());
    verify(runningCommand1).run();
  }

  @Test
  public void testTerminatedCommands() {
    Command terminatedCommand1 = mock(Command.class);
    DefaultLifecycle
        lifecycle = DefaultLifecycle.builder().addTerminatedCommands(terminatedCommand1).build();

    lifecycle.start();
    lifecycle.stop();
    verify(terminatedCommand1).run();
  }

  @Test(expected = IllegalStateException.class)
  public void testRandomTimingShutdownCall() {
    List<Command> startupCommands = new ArrayList<>();
    List<Command> shutdownCommands = new ArrayList<>();

    DefaultLifecycle lifecycle = DefaultLifecycle.builder().build();
    lifecycle.stop();
  }

  @Test(expected = IllegalStateException.class)
  public void testStartMethodCallMultipleTimes() {
    List<Command> startupCommands = new ArrayList<>();
    List<Command> shutdownCommands = new ArrayList<>();

    DefaultLifecycle lifecycle = DefaultLifecycle.builder().build();
    lifecycle.start();
    lifecycle.start();
  }

  @Test(expected = IllegalStateException.class)
  public void testShutdownMethodCallMultipleTimes() {
    DefaultLifecycle lifecycle = DefaultLifecycle.builder().build();
    lifecycle.start();
    lifecycle.stop();
    lifecycle.stop();
  }

  @Test
  public void testAwaitRunning() {
    List<Command> startupCommands = new ArrayList<>();
    List<Command> shutdownCommands = new ArrayList<>();

    final DefaultLifecycle lifecycle = DefaultLifecycle.builder().build();
    lifecycle.start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(10);
          lifecycle.stop();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();

    Assert.assertTrue(lifecycle.isRunning());
    lifecycle.awaitRunning();
    Assert.assertFalse(lifecycle.isRunning());
  }

}
