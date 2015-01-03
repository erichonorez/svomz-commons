package org.svomz.commons.net.http;

import org.eclipse.jetty.servlet.ServletHolder;
import org.svomz.commons.core.Service;

import javax.servlet.Filter;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

public interface HttpServer extends Service {

  void registerServlet(final Class<? extends HttpServlet> servlet, final String path);

  void registerServlet(final ServletHolder servletHolder, final String path);

  void registerFilter(final Class<? extends Filter> filter, final String pathSpec);

  void registerListener(ServletContextListener servletContextListener);

  void addInitParam(final String name, final String value);

}
