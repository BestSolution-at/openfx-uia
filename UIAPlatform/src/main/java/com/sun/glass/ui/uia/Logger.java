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

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import at.bestsolution.uia.ILoggerService;

public class Logger {
  private static boolean LOG = Boolean.getBoolean("uia.log");

  private static Optional<ILoggerService> service;

  static {
    service = findFirst(ServiceLoader.load(ILoggerService.class));
    NativeLibrary.require();
    initIDs();
  }

  private static native void initIDs();

  private static <S> Optional<S> findFirst(ServiceLoader<S> loader) {
    Iterator<S> iterator = loader.iterator();
    if (iterator.hasNext()) {
      return Optional.of(iterator.next());
    } else {
      return Optional.empty();
    }
  }

  public static Logger create(Class<?> cls) {
    return new Logger(service.map(s -> s.createLogger(cls)), "" + cls);
  }

  public static Logger create(String name) {
    return new Logger(service.map(s -> s.createLogger(name)), name);
  }

  private ILoggerService.ILogger logger;
  private String name;

  private Logger(Optional<ILoggerService.ILogger> logger, String name) {
    this.logger = logger.orElse(null);
    this.name = name;
  }

  /* called from native */
  private void logNative(String msg) {
    debug(() -> "[NATIVE] " +  msg);
  }

  public static enum Level {
    DEBUG, WARNING, ERROR, FATAL, INFO
  }

  static ILoggerService.Level mapLevel(Level level) {
    switch (level) {
      case DEBUG:
        return ILoggerService.Level.DEBUG;
      case ERROR:
        return ILoggerService.Level.ERROR;
      case FATAL:
        return ILoggerService.Level.FATAL;
      case WARNING:
        return ILoggerService.Level.WARN;
      case INFO:
        return ILoggerService.Level.INFO;
      default:
        return ILoggerService.Level.INFO;
    }
  }

  public void log(Object source, Level level, Supplier<String> message) {
    log(source, level, message, null);
  }

  public void log(Object source, Level level, Supplier<String> message, Exception e) {
    String sourceString = source == null ? "" : (source + ": ");
    if (logger != null) {
      logger.log(mapLevel(level), () -> sourceString + message.get(), e);
    } else if (LOG) {
      System.err.println("[" + level + "] " + name + " " + sourceString + message.get());
      // LOGGER.log(java.util.logging.Level.INFO, "[" + level + "] " + source + ": " +
      // message.get());
      if (e != null) {
        e.printStackTrace();
      }
    } else {
      // NOOP!
    }
  }

  public void debug(Supplier<String> message) {
    log(null, Level.DEBUG, message);
  }

  public void debug(Object source, Supplier<String> message) {
    log(source, Level.DEBUG, message);
  }

  public void debug(Object source, Supplier<String> message, Exception e) {
    log(source, Level.DEBUG, message, e);
  }

  public void warning(Object source, Supplier<String> message) {
    log(source, Level.WARNING, message);
  }

  public void warning(Object source, Supplier<String> message, Exception e) {
    log(source, Level.WARNING, message, e);
  }

  public void error(Object source, Supplier<String> message) {
    log(source, Level.ERROR, message);
  }

  public void error(Object source, Supplier<String> message, Exception e) {
    log(source, Level.ERROR, message, e);
  }

  public void fatal(Supplier<String> message) {
    log(null, Level.FATAL, message);
  }

  public void fatal(Supplier<String> message, Exception e) {
    log(null, Level.FATAL, message, e);
  }

  public void fatal(Object source, Supplier<String> message) {
    log(source, Level.FATAL, message);
  }

  public void fatal(Object source, Supplier<String> message, Exception e) {
    log(source, Level.FATAL, message, e);
  }

}
