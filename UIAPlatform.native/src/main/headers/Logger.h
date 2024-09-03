#pragma once

#include <jni.h>

#include <source_location>

#include <sstream>

enum class LogLevel {
  LEVEL_TRACE = 0,
  LEVEL_DEBUG = 1,
  LEVEL_INFO = 2,
  LEVEL_WARN = 3,
  LEVEL_ERROR = 4,
  LEVEL_FATAL = 5
};

class Logger;

class LogStream {
private:
  bool m_done = false;;
  Logger& m_logger;
  std::ostringstream m_temp;
  std::source_location m_location;
  LogLevel m_level;

public:
  explicit LogStream(Logger& logger, LogLevel level, std::source_location location = std::source_location::current());

  ~LogStream();

  template<class T>
  LogStream& operator <<(T arg) {
    m_temp << arg;
    return *this;
  }


  void operator <<(decltype(std::endl<char, std::char_traits<char>>));

  void flush();

};

class Logger {
  public:
    Logger(jobject jLogger);
    Logger(const char* name);
    virtual ~Logger();

    void log(const LogLevel& level, const char* msg, std::source_location location = std::source_location::current());

    void trace(const char* msg, std::source_location location = std::source_location::current());
    void debug(const char* msg, std::source_location location = std::source_location::current());
    void info(const char* msg, std::source_location location = std::source_location::current());
    void warn(const char* msg, std::source_location location = std::source_location::current());
    void error(const char* msg, std::source_location location = std::source_location::current());
    void fatal(const char* msg, std::source_location location = std::source_location::current());

    LogStream log(const LogLevel& level, std::source_location location = std::source_location::current());

    LogStream trace(std::source_location location = std::source_location::current());
    LogStream debug(std::source_location location = std::source_location::current());
    LogStream info(std::source_location location = std::source_location::current());
    LogStream warn(std::source_location location = std::source_location::current());
    LogStream error(std::source_location location = std::source_location::current());
    LogStream fatal(std::source_location location = std::source_location::current());

    static void initIDs(JNIEnv* env, jclass cls);

  private:


    jobject jLogger;

    void logNative(LogLevel level, const char* msg, std::source_location location);

    static jclass cls_loggerbridge;
    static jmethodID mid_create;
    static jmethodID mid_logNative;

};
