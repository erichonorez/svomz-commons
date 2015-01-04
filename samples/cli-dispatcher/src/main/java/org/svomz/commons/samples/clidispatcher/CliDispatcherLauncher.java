package org.svomz.commons.samples.clidispatcher;

import com.google.inject.Inject;

import org.svomz.commons.core.Command;

/**
 * Starts the {@link CliDispatcher} when {@link
 * CliDispatcherLauncher#run()} is called.
 */
public class CliDispatcherLauncher implements Command {

  private final CliDispatcher cliDispatcher;

  @Inject
  public CliDispatcherLauncher(CliDispatcher cliDispatcher) {
    this.cliDispatcher = cliDispatcher;
  }

  @Override
  public void run() {
    this.cliDispatcher.start();
  }
}
