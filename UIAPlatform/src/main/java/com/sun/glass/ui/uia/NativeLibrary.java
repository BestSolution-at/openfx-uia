package com.sun.glass.ui.uia;

import java.util.Arrays;

public class NativeLibrary {

  private static Logger LOG;

  public static void require() {

  }

  static {
    at.bestsolution.uia.core.Lib.reportVersionInfo();
    at.bestsolution.uia.Lib.reportVersionInfo();
    try {
      System.load(LibMan.uiaPlatformDll.toString());
    } catch (Exception e) {
      System.err.println("Exception during initialization");
      e.printStackTrace();
    }
    LOG = Logger.create(NativeLibrary.class);
    reportEnvironment();
  }

  


  private static void reportEnvironment() {
		LOG.debug(() -> "Environment: ");
		Arrays.stream(new String[] {
			"java.vendor",
			"java.version", 
			"java.vm.version",
			"javafx.version",
			"javafx.runtime.version"
		}).forEach(prop -> LOG.debug(() -> "\t" + prop + ": " + System.getProperty(prop)));
	}
}
