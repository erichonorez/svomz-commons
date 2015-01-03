package org.svomz.commons.net.http;

import com.google.common.base.Preconditions;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

public class JettyHttpServer implements HttpServer {

  private final ServletContextHandler servletContextHandler;

  private final Server server;

  public JettyHttpServer() {
    this.server = new Server(8080);
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
      // do nothing now
    }
  }

  @Override
  public synchronized void stop() {
    Preconditions.checkState(this.isRunning(), "Jetty server is not running.");

    try {
      this.server.stop();
    } catch (Exception e) {
      // do nothing now
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
