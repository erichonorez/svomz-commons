package org.svomz.commons.samples.cli;

import com.google.inject.*;

import org.svomz.commons.application.AbstractApplication;
import org.svomz.commons.application.AppLauncher;
import org.svomz.commons.application.Lifecycle;
import org.svomz.commons.application.modules.AbstractApplicationModule;
import org.svomz.commons.core.Command;

import java.util.Arrays;

/**
 * The CliDispatcherApp launch a service that wait for input from System.in and execute some action
 * if what the user enters match a configured endpoint.
 */
public class CliDispatcherApp extends AbstractApplication {

  @Override
  public void run() {
    /*
     * We don't want to run or do something since the CliDispatcher is launched by a specific
     * command
     * during the STARTING lifecycle stage.
     */
  }

  @Override
  public Iterable<? extends Module> getModules() {
    return Arrays.asList(new CliDispatcherModule(), new AbstractApplicationModule() {
      @Override
      protected void configure() {

        // Add some command to be executed during lifecycle stages.
        this.onStarting(new Command() {
          @Override
          public void run() {
            System.out.println("Starting...");
          }
        });

        this.onRunning(new Command() {
          @Override
          public void run() {
            System.out.println("Started...");
          }
        });

        this.onStopping(new Command() {
          @Override
          public void run() {
            System.out.println("Stopping...");
          }
        });

        this.onTerminated(new Command() {
          @Override
          public void run() {
            System.out.println("Terminated...");
          }
        });

        // Add some endpoints to the CliDispatcher
        CliDispatcherModule.addEndPoint(binder(), new CliEndPoint("ping") {
          @Override
          public void run() {
            System.out.println("pong");
          }
        });

        CliDispatcherModule.addEndPoint(binder(), new CliEndPoint("quit") {
          @Inject
          private Lifecycle lifecycle;

          @Override
          public void run() {
            this.lifecycle.stop();
          }
        });

        CliDispatcherModule.addEndPoint(binder(), new CliEndPoint("abort") {
          @Inject
          private Lifecycle lifecycle;

          @Override
          public void run() {
            this.lifecycle.abort();
          }
        });
      }
    });
  }

  public static void main(String[] args) {
    AppLauncher.launch(CliDispatcherApp.class);
  }
}
