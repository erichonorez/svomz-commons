package org.svomz.commons.application.module;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.Assert;

import org.junit.Test;
import org.svomz.commons.application.Lifecycle;
import org.svomz.commons.application.modules.LifecycleModule;

public class LifecycleModuleUnitTest {

  @Test
  public void testCreationViaInjector() {
    Injector injector = Guice.createInjector(new LifecycleModule());
    Lifecycle lifecycle1 = injector.getInstance(Lifecycle.class);
    Lifecycle lifecycle2 = injector.getInstance(Lifecycle.class);
    Assert.assertEquals(lifecycle1, lifecycle2);
  }

}
