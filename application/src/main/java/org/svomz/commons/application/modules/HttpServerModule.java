package org.svomz.commons.application.modules;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;

import org.svomz.commons.core.Command;
import org.svomz.commons.net.http.HttpServer;
import org.svomz.commons.net.http.JettyHttpServer;

import javax.servlet.ServletContextListener;

public class HttpServerModule extends AbstractModule {

  @Override
  protected void configure() {
    this.bind(HttpServer.class).to(JettyHttpServer.class).in(Singleton.class);
    this.bind(AppServletConfig.class);
    this.bind(HttpServerLauncher.class);

    LifecycleModule.addStartingCommand(binder(), HttpServerLauncher.class);
    LifecycleModule.addStoppingCommand(binder(), HttpServerKiller.class);
  }

  protected static class HttpServerLauncher implements Command {

    private final HttpServer httpServer;
    private ServletContextListener appServletsConfig;

    @Inject
    public HttpServerLauncher(final HttpServer httpServer, final AppServletConfig appServletConfig) {
      Preconditions.checkNotNull(httpServer);
      Preconditions.checkNotNull(appServletConfig);

      this.httpServer = httpServer;
      this.appServletsConfig = appServletConfig;
    }

    @Override
    public void run() {
      // To filter all request to the guice filter and thus guicify servlets.
      this.httpServer.registerFilter(GuiceFilter.class, "/*");
      // To configure servlet
      this.httpServer.registerListener(this.appServletsConfig);

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
