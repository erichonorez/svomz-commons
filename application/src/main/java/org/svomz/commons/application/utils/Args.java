package org.svomz.commons.application.utils;

import com.google.inject.Binder;
import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Utility class to bind system properties to
 */
public class Args {

  private Args() {
    throw new RuntimeException();
  }

  public static void integer(final Binder binder, final String key) {
    binder.bind(Integer.class)
      .annotatedWith(Cmd.class)
      .toInstance(Integer.parseInt(System.getProperty(key)));
  }

  public static void literal(final Binder binder, final String key) {
    binder.bind(String.class)
      .annotatedWith(Cmd.class)
      .toInstance(System.getProperty(key));
  }

  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD})
  public @interface Cmd { }

}
