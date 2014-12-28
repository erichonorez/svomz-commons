package org.svomz.commons.samples.http;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import org.svomz.commons.application.AbstractApplication;
import org.svomz.commons.application.AppLauncher;
import org.svomz.commons.application.Lifecycle;
import org.svomz.commons.application.modules.HttpServerModule;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Launches an http server and with
 */
public class HttpApp extends AbstractApplication {

  @Override
  public void run() {
    // do nothing
  }

  public Iterable<? extends Module> getModules() {
    return Arrays.asList(new HttpServerModule(), new ServletModule() {
      @Override
      protected void configureServlets() {
        this.bind(HealthServlet.class).in(Singleton.class);
        this.serve("/health").with(HealthServlet.class);

        this.bind(QuitServlet.class).in(Singleton.class);
        this.serve("/quit").with(QuitServlet.class);

        this.bind(LogRequestFilter.class).in(Singleton.class);
        this.filter("/*").through(LogRequestFilter.class);

        this.bind(StatsServlet.class).in(Singleton.class);
        this.serve("/stats").with(StatsServlet.class);
      }
    });
  }

  public static void main(String[] args) {
    AppLauncher.launch(HttpApp.class);
  }

  public static class QuitServlet extends HttpServlet {

    private final Lifecycle lifecycle;

    @Inject
    public QuitServlet(Lifecycle lifecycle) {
      Preconditions.checkNotNull(lifecycle);

      this.lifecycle = lifecycle;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
      this.lifecycle.stop();
    }
  }

  public static class StatsServlet extends HttpServlet {

    private final LogRequestFilter filter;

    @Inject
    public StatsServlet(final LogRequestFilter filter) {
      Preconditions.checkNotNull(filter);

      this.filter = filter;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
      response.setContentType("text/plain");
      response.setStatus(200);
      try {
        response.getWriter().print(this.filter.getTotal());
      } catch (IOException e) {
        //do nothing
      }
    }
  }

  public static class LogRequestFilter implements Filter {

    private AtomicLong requestCounter;

    public LogRequestFilter() {
      this.requestCounter = new AtomicLong();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
      this.requestCounter.set(0);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
      this.requestCounter.getAndIncrement();
      filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
      // do nothing.
    }

    public long getTotal() {
      return this.requestCounter.longValue();
    }
  }

  public static class HealthServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("text/plain");
      response.setStatus(200);
      response.getWriter().println("ok");
    }

  }
}
