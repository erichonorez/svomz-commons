package org.svomz.commons.samples.cli;

import com.google.inject.Inject;

import org.svomz.commons.core.Command;

/**
 * Kills the {@link org.svomz.commons.samples.cli.CliDispatcher} when method {@link
 * CliDispatcherKiller#run()} is called.
 */
public class CliDispatcherKiller implements Command {

  private final CliDispatcher cliDispatcher;

  @Inject
  public CliDispatcherKiller(CliDispatcher cliDispatcher) {
    this.cliDispatcher = cliDispatcher;
  }

  @Override
  public void run() {
    this.cliDispatcher.stop();
  }

}
