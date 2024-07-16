package at.bestsolution.uia.agent.internal;

import java.util.function.Supplier;

public interface AgentLogger {

  static class LocationData {
    String lang;
    String fileName;
    int lineNumber;
    String functionName;
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
    StackTraceElement callFrame = Thread.currentThread().getStackTrace()[2 + depth];
    data.lang = "J";
    data.functionName = callFrame.getClassName() + "." + callFrame.getMethodName();
    data.fileName = callFrame.getFileName();
    data.lineNumber = callFrame.getLineNumber();
    return data;
  }

}
