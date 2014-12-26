package org.svomz.commons.application;

import com.google.inject.Module;

/**
 * An application that supports a limited lifecycle and optional binding of guice modules.
 */
public interface Application extends Runnable {

  /**
   * Returns binding modules for the application.
   *
   * @return Application binding modules.
   */
  Iterable<? extends Module> getModules();
}
