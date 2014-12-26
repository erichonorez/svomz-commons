package org.svomz.commons.application;

import com.google.inject.Module;

import java.util.Collections;

/**
 * Base abstract application that returns an empty list of binding modules
 */
public abstract class AbstractApplication implements Application {

  @Override
  public Iterable<? extends Module> getModules() {
    return Collections.emptyList();
  }

}
