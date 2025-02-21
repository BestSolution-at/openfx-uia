
#include "common.h"
#include "Logger.h"

#include <iostream>

LogStream::LogStream(Logger& logger, LogLevel level, std::source_location location) : m_logger(logger), m_level(level), m_location(location) {

}

LogStream::~LogStream() {
  if (!m_done) {
    m_temp << " (no endl or flush)";
    flush();
  }
}

void LogStream::flush() {
  m_logger.log(m_level, m_temp.str().c_str(), m_location);
  m_temp.flush();
  m_done = true;
}

void LogStream::operator <<(decltype(std::endl<char, std::char_traits<char>>)) {
  flush();
}


jclass Logger::cls_loggerbridge = nullptr;
jmethodID Logger::mid_create;
jmethodID Logger::mid_logNative;

Logger::Logger(jobject jLogger) {
  this->jLogger = GetEnv()->NewGlobalRef(jLogger);
}

Logger::Logger(const char* name) {
  jstring jName = GetEnv()->NewStringUTF(name);
  jobject jLogger = GetEnv()->CallStaticObjectMethod(cls_loggerbridge, mid_create, jName);
  GetEnv()->DeleteLocalRef(jName);
  this->jLogger = GetEnv()->NewGlobalRef(jLogger);
}

Logger::~Logger() {
  GetEnv()->DeleteGlobalRef(jLogger);
}

void Logger::log(const LogLevel& level, const char* msg, std::source_location location) {
  logNative(level, msg, location);
}

void Logger::trace(const char* msg, std::source_location location) {
  log(LogLevel::LEVEL_TRACE, msg, location);
}

void Logger::debug(const char* msg, std::source_location location) {
  log(LogLevel::LEVEL_DEBUG, msg, location);
}

void Logger::info(const char* msg, std::source_location location) {
  log(LogLevel::LEVEL_INFO, msg, location);
}

void Logger::warn(const char* msg, std::source_location location) {
  log(LogLevel::LEVEL_WARN, msg, location);
}

void Logger::error(const char* msg, std::source_location location) {
  log(LogLevel::LEVEL_ERROR, msg, location);
}

void Logger::fatal(const char* msg, std::source_location location) {
  log(LogLevel::LEVEL_FATAL, msg, location);
}

LogStream Logger::log(const LogLevel& level, std::source_location location) {
  return LogStream{*this, level, location};
}

LogStream Logger::trace(std::source_location location) {
  return LogStream{*this, LogLevel::LEVEL_TRACE, location};
}
LogStream Logger::debug(std::source_location location) {
  return LogStream{*this, LogLevel::LEVEL_DEBUG, location};
}
LogStream Logger::info(std::source_location location) {
  return LogStream{*this, LogLevel::LEVEL_INFO, location};
}
LogStream Logger::warn(std::source_location location) {
  return LogStream{*this, LogLevel::LEVEL_WARN, location};
}
LogStream Logger::error(std::source_location location) {
  return LogStream{*this, LogLevel::LEVEL_ERROR, location};
}
LogStream Logger::fatal(std::source_location location) {
  return LogStream{*this, LogLevel::LEVEL_FATAL, location};
}


void Logger::logNative(LogLevel level, const char* msg, std::source_location location) {
  auto j_level = static_cast<jint>(level);
  auto j_msg = GetEnv()->NewStringUTF(msg);
  auto j_file = GetEnv()->NewStringUTF(location.file_name());
  auto j_line = static_cast<jint>(location.line());
  auto j_func = GetEnv()->NewStringUTF(location.function_name());

  GetEnv()->CallStaticVoidMethod(cls_loggerbridge, mid_logNative, jLogger, j_level, j_msg, j_file, j_line, j_func);

  GetEnv()->DeleteLocalRef(j_msg);
  GetEnv()->DeleteLocalRef(j_file);
  GetEnv()->DeleteLocalRef(j_func);
}

void Logger::initIDs(JNIEnv* env, jclass jClass) {
  cls_loggerbridge = (jclass) env->NewGlobalRef(jClass);
  mid_create = env->GetStaticMethodID(cls_loggerbridge, "create", "(Ljava/lang/String;)Lat/bestsolution/uia/Logger;");
  mid_logNative = env->GetStaticMethodID(cls_loggerbridge, "logNative", "(Lat/bestsolution/uia/Logger;ILjava/lang/String;Ljava/lang/String;ILjava/lang/String;)V");
}

extern "C" JNIEXPORT void JNICALL Java_at_bestsolution_uia_LoggerNativeBridge_initIDs
  (JNIEnv *env, jclass jClass) {
    Logger::initIDs(env, jClass);
}
