package at.bestsolution.uia;

import java.util.function.Supplier;

public interface ILoggerService {
  
  static enum Level {
    TRACE, DEBUG, INFO, WARN, ERROR, FATAL
  }

  static interface ILogger {
    void log(Level level, Supplier<String> msg, Throwable ex);

    boolean isEnabledForLevel(Level level);
  }

  ILogger createLogger(String name);
  ILogger createLogger(Class<?> cls);

}
