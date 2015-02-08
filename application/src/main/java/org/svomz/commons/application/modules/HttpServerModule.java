package org.svomz.commons.application.modules;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;

import org.svomz.commons.application.utils.Args;
import org.svomz.commons.core.Command;
import org.svomz.commons.net.http.HttpServer;
import org.svomz.commons.net.http.JettyHttpServer;

import java.util.Set;

import javax.servlet.ServletContextListener;

public class HttpServerModule extends AbstractModule {

  @Override
  protected void configure() {
    Args.integer(this.binder(), "http.port");

    this.bind(HttpServer.class).toProvider(new Provider<HttpServer>() {
      @Inject
      @Args.Cmd
      private int port;

      @Override
      public HttpServer get() {
        return new JettyHttpServer(this.port);
      }
    }).in(Singleton.class);

    HttpServerModule.addContextListener(this.binder(), AppServletConfig.class);
    LifecycleModule.addStartingCommand(binder(), HttpServerLauncher.class);
    LifecycleModule.addStoppingCommand(binder(), HttpServerKiller.class);
  }

  public static void addContextListener(final Binder binder, final ServletContextListener contextListener) {
    HttpServerModule.contextListenerBinder(binder).addBinding().toInstance(contextListener);
  }

  public static void addContextListener(final Binder binder, final Class<? extends  ServletContextListener> contextListener) {
    HttpServerModule.contextListenerBinder(binder).addBinding().to(contextListener);
  }

  private static Multibinder<ServletContextListener> contextListenerBinder(final Binder binder) {
    return Multibinder.newSetBinder(binder, ServletContextListener.class);
  }

  protected static class HttpServerLauncher implements Command {

    private final HttpServer httpServer;
    private final Set<ServletContextListener> contextListeners;

    @Inject
    public HttpServerLauncher(final HttpServer httpServer,
      final Set<ServletContextListener> contextListeners) {
      Preconditions.checkNotNull(httpServer);
      Preconditions.checkNotNull(contextListeners);

      this.httpServer = httpServer;
      this.contextListeners = contextListeners;
    }

    @Override
    public void run() {
      // To filter all request to the guice filter and thus guicify servlets.
      this.httpServer.registerFilter(GuiceFilter.class, "/*");
      // To configure servlet
      for (ServletContextListener contextListener : this.contextListeners) {
        this.httpServer.registerListener(contextListener);
      }

      this.httpServer.start();
    }
  }

  protected static class HttpServerKiller implements Command {

    private final HttpServer httpServer;
    @Inject
    public HttpServerKiller(final HttpServer httpServer) {
      Preconditions.checkNotNull(httpServer);

      this.httpServer = httpServer;
    }

    @Override
    public void run() {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            Thread.currentThread().sleep(1000);
            HttpServerKiller.this.httpServer.stop();
          } catch (Exception e) {
          }
        }
      }).start();
    }
  }

  protected static class AppServletConfig extends GuiceServletContextListener {

    private final Injector injector;

    @Inject
    public AppServletConfig(final Injector injector) {
      Preconditions.checkNotNull(injector);

      this.injector = injector;
    }

    @Override
    protected Injector getInjector() {
      return this.injector;
    }
  }

}
