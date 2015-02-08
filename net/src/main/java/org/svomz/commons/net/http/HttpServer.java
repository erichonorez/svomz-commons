package org.svomz.commons.net.http;

import org.eclipse.jetty.servlet.ServletHolder;
import org.svomz.commons.core.Service;

import javax.servlet.Filter;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

/**
 * Creates an Jetty HTTP server on a specific port.
 *
 * The service can be started and stopped by calling {@link JettyHttpServer#start()} and {@link JettyHttpServer#stop()}
 * respectively.
 *
 */
public interface HttpServer extends Service {

  /**
   * Registers a servlet class to the http server.
   *
   * @param servlet the servlet class
   * @param path the url served by the http servlet
   */
  void registerServlet(final Class<? extends HttpServlet> servlet, final String path);

  /**
   * Registers a servlet holder to the http server.
   *
   * @param servletHolder the servlet holder instance
   * @param path the url served by the servlet holder
   */
  void registerServlet(final ServletHolder servletHolder, final String path);

  /**
   * Registers a servlet identified by its class FQN.
   *
   * @param className the servlet's class name
   * @param path the url served by the servlet
   */
  void registerServlet(final String className, final String path);

  /**
   * Registers a filter class to the http server.
   *
   * @param filter the filter class
   * @param pathSpec the url filtered
   */
  void registerFilter(final Class<? extends Filter> filter, final String pathSpec);

  /**
   * Register a servlet context listener to the http server.
   *
   * @param servletContextListener
   */
  void registerListener(ServletContextListener servletContextListener);

  /**
   * Adds initial parameter to the http server.
   *
   * @param name the init param name
   * @param value the init param value
   */
  void addInitParam(final String name, final String value);

}
