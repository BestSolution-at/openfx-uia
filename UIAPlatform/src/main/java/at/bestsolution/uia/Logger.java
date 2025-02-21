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
package at.bestsolution.uia;

import java.util.function.Supplier;

public interface Logger {

  static class LocationData {
    public String lang;
    public String fileName;
    public int lineNumber;
    public String functionName;
  }

  public static enum Level {
    TRACE, DEBUG, INFO, WARNING, ERROR, FATAL
  }

  void log(Object source, Level level, Supplier<String> message, Throwable e, LocationData loc);

  default void trace(Object source, Supplier<String> message, Throwable e) {
    log(source, Level.TRACE, message, e, getLocationData(1));
  }
  default void trace(Object source, Supplier<String> message) {
    log(source, Level.TRACE, message, null, getLocationData(1));
  }
  default void trace(Supplier<String> message, Throwable e) {
    log(null, Level.TRACE, message, e, getLocationData(1));
  }
  default void trace(Supplier<String> message) {
    log(null, Level.TRACE, message, null, getLocationData(1));
  }

  default void debug(Object source, Supplier<String> message, Throwable e) {
    log(source, Level.DEBUG, message, e, getLocationData(1));
  }
  default void debug(Object source, Supplier<String> message) {
    log(source, Level.DEBUG, message, null, getLocationData(1));
  }
  default void debug(Supplier<String> message, Throwable e) {
    log(null, Level.DEBUG, message, e, getLocationData(1));
  }
  default void debug(Supplier<String> message) {
    log(null, Level.DEBUG, message, null, getLocationData(1));
  }

  default void info(Object source, Supplier<String> message, Throwable e) {
    log(source, Level.INFO, message, e, getLocationData(1));
  }
  default void info(Object source, Supplier<String> message) {
    log(source, Level.INFO, message, null, getLocationData(1));
  }
  default void info(Supplier<String> message, Throwable e) {
    log(null, Level.INFO, message, e, getLocationData(1));
  }
  default void info(Supplier<String> message) {
    log(null, Level.INFO, message, null, getLocationData(1));
  }

  default void warning(Object source, Supplier<String> message, Throwable e) {
    log(source, Level.WARNING, message, e, getLocationData(1));
  }
  default void warning(Object source, Supplier<String> message) {
    log(source, Level.WARNING, message, null, getLocationData(1));
  }
  default void warning(Supplier<String> message, Throwable e) {
    log(null, Level.WARNING, message, e, getLocationData(1));
  }
  default void warning(Supplier<String> message) {
    log(null, Level.WARNING, message, null, getLocationData(1));
  }

  default void error(Object source, Supplier<String> message, Throwable e) {
    log(source, Level.ERROR, message, e, getLocationData(1));
  }
  default void error(Object source, Supplier<String> message) {
    log(source, Level.ERROR, message, null, getLocationData(1));
  }
  default void error(Supplier<String> message, Throwable e) {
    log(null, Level.ERROR, message, e, getLocationData(1));
  }
  default void error(Supplier<String> message) {
    log(null, Level.ERROR, message, null, getLocationData(1));
  }

  default void fatal(Object source, Supplier<String> message, Throwable e) {
    log(source, Level.FATAL, message, e, getLocationData(1));
  }
  default void fatal(Object source, Supplier<String> message) {
    log(source, Level.FATAL, message, null, getLocationData(1));
  }
  default void fatal(Supplier<String> message, Throwable e) {
    log(null, Level.FATAL, message, e, getLocationData(1));
  }
  default void fatal(Supplier<String> message) {
    log(null, Level.FATAL, message, null, getLocationData(1));
  }

  boolean isLevel(Level level);


  static LocationData getLocationData(int depth) {
    LocationData data = new LocationData();
    StackTraceElement[] frames = Thread.currentThread().getStackTrace();
    StackTraceElement callFrame = frames[2 + depth];
    data.lang = "J";
    data.functionName = callFrame.getClassName() + "." + callFrame.getMethodName();
    data.fileName = callFrame.getFileName();
    data.lineNumber = callFrame.getLineNumber();
    return data;
  }

  static Logger create(Class<?> cls) {
    return LoggerFactory.create(cls);
  }
  static Logger create(String name) {
    return LoggerFactory.create(name);
  }

}
