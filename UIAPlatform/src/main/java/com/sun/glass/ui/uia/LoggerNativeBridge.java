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

import com.sun.glass.ui.uia.Logger.Level;

public class LoggerNativeBridge {
  private static final Logger LOG = LoggerFactory.create(LoggerNativeBridge.class);

  static {
    NativeLibrary.require();
    initIDs();
  }

  public static void require() {
    LOG.trace(() -> "require()");
  }

  private static native void initIDs();

  /* called from native */
  private static Logger create(String name) {
    return com.sun.glass.ui.uia.LoggerFactory.create(name);
  }

  /* called from native */
  private static void logNative(Logger logger, int level, String msg, String file, int line, String func) {
    Logger.LocationData loc = new Logger.LocationData();
    loc.lang = "C";
    loc.fileName = file;
    loc.lineNumber = line;
    loc.functionName = func;
    logger.log(null, convertNativeLevel(logger, level), () -> msg, null, loc);
  }

  private static Level convertNativeLevel(Logger logger, int nativeInt) {
    if (nativeInt < 0 || nativeInt >= Level.values().length) {
      logger.warning(() -> "could not convert native loglevel: " + nativeInt + ". using WARNING");
      return Level.WARNING;
    }
    return Level.values()[nativeInt];
  }

}
