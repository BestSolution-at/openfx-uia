package at.bestsolution.uia.logger;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;

import at.bestsolution.uia.ILoggerService;

public class SLF4JLoggerService implements ILoggerService {

  public static class SLF4JLogger implements ILogger {

    private Logger logger;

    public SLF4JLogger(Logger logger) {
      this.logger = logger;
    }


    @Override
    public void log(Level level, Supplier<String> msg, Throwable ex) {
      LoggingEventBuilder builder = logger.atLevel(mapLevel(level));
      if (ex != null) {
        builder.setCause(ex);
      }
      builder.log(msg);
    }

    @Override
    public boolean isEnabledForLevel(Level level) {
      return logger.isEnabledForLevel(mapLevel(level));
    }

  }

  static org.slf4j.event.Level mapLevel(Level level) {
    switch (level) {
      case DEBUG: return org.slf4j.event.Level.DEBUG;
      case ERROR: return org.slf4j.event.Level.ERROR;
      case FATAL: return org.slf4j.event.Level.ERROR;
      case INFO: return org.slf4j.event.Level.INFO;
      case TRACE: return org.slf4j.event.Level.TRACE;
      case WARN: return org.slf4j.event.Level.WARN;
      default: return org.slf4j.event.Level.INFO;
    }
  }

  @Override
  public ILogger createLogger(String name) {
    return new SLF4JLogger(LoggerFactory.getLogger(name));
  }

  @Override
  public ILogger createLogger(Class<?> cls) {
    return new SLF4JLogger(LoggerFactory.getLogger(cls));
  }


  
}
