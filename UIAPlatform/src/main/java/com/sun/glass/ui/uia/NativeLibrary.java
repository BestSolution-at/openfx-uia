/*
 * -----------------------------------------------------------------
 * Copyright (c) 2021 BestSolution.at EDV Systemhaus GmbH
 * All Rights Reserved.
 *
 * BestSolution.at MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE  OR NON - INFRINGEMENT.
 * BestSolution.at SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * This software is released under the terms of the
 *
 *                  "GNU General Public License, Version 2
 *                         with classpath exception"
 *
 * and may only be distributed and used under the terms of the
 * mentioned license. You should have received a copy of the license
 * along with this software product, if not you can download it from
 * http://www.gnu.org/licenses/gpl.html
 * ----------------------------------------------------------------
 */
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
    // first initialize native logger
    LoggerNativeBridge.require();
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
