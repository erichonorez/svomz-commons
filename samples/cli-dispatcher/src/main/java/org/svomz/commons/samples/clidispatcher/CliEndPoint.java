package org.svomz.commons.samples.clidispatcher;

import com.google.common.base.Preconditions;

import org.svomz.commons.core.Command;

/**
 * A {@link CliEndPoint} is a {@link org.svomz.commons.core.Command}
 * identified by a noun.
 */
public abstract class CliEndPoint implements Command {

  private final String path;

  public CliEndPoint(final String path) {
    this.path = Preconditions.checkNotNull(path);
  }

  /**
   * Run the command if the supplied path match the endpoint's path.
   *
   * @param path
   */
  public void runIfMatch(String path) {
    if (!this.path.equals(path)) {
      return;
    }
    this.run();
  }

  @Override
  public abstract void run();
}
