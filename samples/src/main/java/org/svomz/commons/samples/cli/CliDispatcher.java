package org.svomz.commons.samples.cli;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import org.svomz.commons.core.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Set;

/**
 * Service that listen for inputs from System.in and executes commands according to what the user
 * enters.
 *
 * A CliDispatcher is composed by a fixed set of {@link org.svomz.commons.samples.cli.CliEndPoint}s.
 * When the user enters something on the prompt the dispatcher will loop over its set of  endpoints
 * and call the {@link org.svomz.commons.samples.cli.CliEndPoint#runIfMatch(String)} method on each
 * of them.
 */
public class CliDispatcher implements Runnable {

  private final Set<CliEndPoint> endpoints;
  private volatile boolean isRunning;

  @Inject
  public CliDispatcher(Set<CliEndPoint> endpoints) {
    Preconditions.checkNotNull(endpoints);

    this.endpoints = Collections.unmodifiableSet(endpoints);
  }

  @Override
  public void run() {
    this.isRunning = true;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    while (this.isRunning) {
      String commandInput;
      try {
        commandInput = br.readLine();
      } catch (IOException e) {
        continue;
      }

      for (CliEndPoint endPoint : this.endpoints) {
        endPoint.runIfMatch(commandInput);
      }
    }
  }

  /**
   * Starts the service in a new Thread.
   */
  public void start() {
    Preconditions.checkState(this.isRunning == false, "CliDispatcher already started.");
    new Thread(this).start();
  }

  /**
   * Stops the service.
   */
  public void stop() {
    this.isRunning = false;
  }
}




