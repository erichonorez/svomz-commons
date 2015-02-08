package org.svomz.commons.net.http;

import com.google.common.base.Preconditions;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.EnumSet;
import java.util.logging.Logger;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

/**
 * Implements {@link org.svomz.commons.net.http.HttpServer} using Jetty.
 *
 * This simple implementation uses a {@link org.eclipse.jetty.servlet.ServletContextHandler} with "/"
 * as context path.
 *
 * This implementation has no session nor security handlers.
 */
public class JettyHttpServer implements HttpServer {

  private final static Logger LOG = Logger.getLogger(JettyHttpServer.class.getName());

  private final ServletContextHandler servletContextHandler;

  private final Server server;

  /**
   * @param port the port on which the http server will listen to
   */
  public JettyHttpServer(final int port) {
    Preconditions.checkArgument(port >= 1 && port <= 65535, "Invalid port range.");

    this.server = new Server(port);
    this.servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
    this.servletContextHandler.setContextPath("/");
    this.server.setHandler(this.servletContextHandler);
  }

  @Override
  public synchronized void start() {
    Preconditions.checkState(!this.isRunning(), "Jetty server can only be started once.");

    try {
      this.server.start();
    } catch (Exception ex) {
      //TODO log exception
    }
  }


  @Override
  public synchronized void stop() {
    Preconditions.checkState(this.isRunning(), "Jetty server is not running.");

    try {
      this.server.stop();
    } catch (Exception e) {
      //TODO log exception
    }
  }

  @Override
  public boolean isRunning() {
    return this.server.isRunning();
  }

  @Override
  public void registerServlet(Class<? extends HttpServlet> servlet, String path) {
    Preconditions.checkNotNull(servlet);
    Preconditions.checkNotNull(path);

    this.servletContextHandler.addServlet(servlet, path);
  }

  @Override
  public void registerServlet(ServletHolder servletHolder, String path) {
    Preconditions.checkNotNull(servletHolder);
    Preconditions.checkNotNull(path);

    this.servletContextHandler.addServlet(servletHolder, path);
  }

  @Override
  public void registerServlet(String className, String path) {
    Preconditions.checkNotNull(className);
    Preconditions.checkNotNull(path);

    this.servletContextHandler.addServlet(className, path);
  }

  @Override
  public void registerFilter(Class<? extends Filter> filter, String pathSpec) {
    Preconditions.checkNotNull(filter);
    Preconditions.checkNotNull(pathSpec);

    this.servletContextHandler.addFilter(filter, pathSpec, EnumSet.of(DispatcherType.REQUEST));
  }

  @Override
  public void registerListener(ServletContextListener servletContextListener) {
    Preconditions.checkNotNull(servletContextListener);

    this.servletContextHandler.addEventListener(servletContextListener);
  }

  @Override
  public void addInitParam(final String name, final String value) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(value);

    this.servletContextHandler.setInitParameter(name, value);
  }

}
