package org.svomz.commons.samples.cli;

import com.google.inject.Inject;

import org.svomz.commons.core.Command;

/**
 * Starts the {@link org.svomz.commons.samples.cli.CliDispatcher} when {@link
 * org.svomz.commons.samples.cli.CliDispatcherLauncher#run()} is called.
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
