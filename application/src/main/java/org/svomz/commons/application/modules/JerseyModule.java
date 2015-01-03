package org.svomz.commons.application.modules;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.svomz.commons.core.Command;
import org.svomz.commons.net.http.HttpServer;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Adds Restful web services capabilities to the application.
 *
 * This module has a dependency to {@link org.svomz.commons.net.http.HttpServer} which could be
 * resolved by the use of the {@link org.svomz.commons.application.modules.HttpServerModule}.
 *
 * To properly configure Jersey you need to configure the resource config class to use. This is achived
 * by calling {@link org.svomz.commons.application.modules.JerseyModule#resourceConfig(com.google.inject.Binder, Class)}
 * from your application module.
 */
public class JerseyModule extends AbstractModule {

  @Override
  protected void configure() {
    LifecycleModule.addRunningCommand(binder(), JerseyServletInstaller.class);
    this.requestStaticInjection(AppInjector.class);
  }

  /**
   * Configures a {@link org.glassfish.jersey.server.ResourceConfig} class to be used to configure the Jersey
   * stack.
   *
   * @param binder the binder of you application module.
   * @param resourceConfig the class to be use to configure jersey
   */
  public static void resourceConfig(Binder binder, Class<? extends ResourceConfig> resourceConfig) {
    binder.bind(new TypeLiteral<Class<? extends ResourceConfig>>() {}).toInstance(resourceConfig);
  }

  /**
   * Deploys the Jersey application configured by a {@link org.glassfish.jersey.server.ResourceConfig}
   * onto a {@link org.svomz.commons.net.http.HttpServer}.
   */
  private static class JerseyServletInstaller implements Command {

    private final HttpServer httpServer;
    private final Class<? extends ResourceConfig> resourceConfig;

    @Inject
    public JerseyServletInstaller(final HttpServer httpServer,
                                  final Class<? extends ResourceConfig> resourceConfig) {
      Preconditions.checkNotNull(httpServer);
      Preconditions.checkNotNull(resourceConfig);

      this.httpServer = httpServer;
      this.resourceConfig = resourceConfig;
    }

    /**
     * Registers an instance of the Jersey's {@link org.glassfish.jersey.servlet.ServletContainer}
     * to a {@link org.svomz.commons.net.http.HttpServer}.
     *
     * It also configures the "javax.ws.rs.Application" initialization property of the ServletContainer
     * to the fully qualified name of a {@link org.glassfish.jersey.server.ResourceConfig}.
     *
     * After this command have been successfully run the Jersey's resources defined in the {@link org.glassfish.jersey.server.ResourceConfig}
     * are deployed onto the {@link org.svomz.commons.net.http.HttpServer}.
     *
     *
     * The ServletContainer is configured to serve "/*".
     */
    @Override
    public void run() {
      ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
      servletHolder.setInitParameter("javax.ws.rs.Application", this.resourceConfig.getName());

      this.httpServer.registerServlet(servletHolder, "/*");
    }
  }

  /**
   * Configures the {@link org.glassfish.hk2.api.ServiceLocator} injected by Jersey to be able to inject
   * the application's Guice services.
   *
   * To enable this feature in the ResourceConfig you just have to call this method in the constructor:
   * <code>
   *   register(AppInjector.class);
   * </code>
   */
  public static final class AppInjector implements Feature {

    // Injected by the Guice's Injector itself during the Jersey module configuration.
    @Inject
    private static Injector injector;

    // Injected by Jersey.
    @javax.inject.Inject
    private ServiceLocator serviceLocator;

    /**
     * Configures the {@link org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge} retrieved via
     * {@link org.glassfish.hk2.api.ServiceLocator} with a {@link com.google.inject.Injector}.
     */
    @Override
    public boolean configure(FeatureContext context) {
      Preconditions.checkState(injector != null, "Application injector is not injected.");
      Preconditions.checkState(serviceLocator != null, "Jersey service locator is not injected.");

      GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
      GuiceIntoHK2Bridge bridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
      bridge.bridgeGuiceInjector(injector);

      return true;
    }
  }

}
