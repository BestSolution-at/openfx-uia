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
package at.bestsolution.uia.internal;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class LoggerFactory {

  private static class DelegateLogger implements Logger {

    private ILoggerService.ILogger logger;

    public DelegateLogger(ILoggerService.ILogger logger) {
      this.logger = logger;
    }

    @Override
    public void log(Object source, Level level, Supplier<String> message, Throwable e, LocationData loc) {
      logger.log(mapLevel(level), withSource(message, source), e);
    }

    @Override
    public boolean isLevel(Level level) {
      return logger.isEnabledForLevel(mapLevel(level));
    }

    static ILoggerService.Level mapLevel(Level level) {
      switch (level) {
        case TRACE:
          return ILoggerService.Level.TRACE;
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
  }

  private static class StdErrLogger implements Logger {
    private final static boolean LOG = Boolean.getBoolean("uia.log");
    private final static int LOG_LEVEL = Integer.getInteger("uia.loglevel", 2);

    private final String name;

    private StdErrLogger(String name) {
      this.name = name;
    }

    @Override
    public void log(Object source, Level level, Supplier<String> message, Throwable e, LocationData loc) {
      if (!isLevel(level)) {
        return;
      }
      try {
        String lang = loc != null && loc.lang != null ? loc.lang + " " : "";
        String location = loc != null ? loc.functionName + "("+loc.fileName+":"+loc.lineNumber+"): " : "";
        System.err.println(lang + "[" + level + "] " + name + " " + location + " " + withSource(message, source).get());
        if (e != null) {
          System.err.print(lang + "[" + level + "] ");
          e.printStackTrace();
        }
      } catch (Exception ex) {
        System.err.print("[ERROR] Logger exception: ");
        ex.printStackTrace();
      }
    }

    public boolean isLevel(Level level) {
      return LOG && LOG_LEVEL <= level.ordinal();
    }
  }

  private static Supplier<String> withSource(Supplier<String> message, Object source) {
    String sourceString = source == null ? "" : (source + ": ");
    return () -> sourceString + message.get();
  }

  private static Optional<ILoggerService> service;

  static {
    service = findFirst(ServiceLoader.load(ILoggerService.class));
  }

  private static <S> Optional<S> findFirst(ServiceLoader<S> loader) {
    Iterator<S> iterator = loader.iterator();
    if (iterator.hasNext()) {
      return Optional.of(iterator.next());
    } else {
      return Optional.empty();
    }
  }

  public static Logger create(Class<?> cls) {
    if (service.isPresent()) {
      return new DelegateLogger(service.get().createLogger(cls));
    } else {
      return new StdErrLogger(cls.getName());
    }
  }

  public static Logger create(String name) {
    if (service.isPresent()) {
      return new DelegateLogger(service.get().createLogger(name));
    } else {
      return new StdErrLogger(name);
    }
  }

}
