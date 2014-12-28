package org.svomz.commons.net.http;

import org.svomz.commons.core.Service;

import javax.servlet.Filter;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

public interface HttpServer extends Service {

  void registerServlet(final Class<? extends HttpServlet> servlet, final String path);

  void registerFilter(final Class<? extends Filter> filter, final String pathSpec);

  void registerListener(ServletContextListener servletContextListener);

}
