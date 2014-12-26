package org.svomz.commons.application;

/**
 * Created by eric on 23/12/14.
 */
public interface Lifecycle {

  void start();
  void stop();
  void abort();
  void awaitRunning();

}
