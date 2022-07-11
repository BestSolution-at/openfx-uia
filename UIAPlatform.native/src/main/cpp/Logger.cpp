
#include "common.h"
#include "Logger.h"

jclass Logger::cls_logger;
jmethodID Logger::mid_create;
jmethodID Logger::mid_logNative;

Logger::Logger(jobject jLogger) {
  this->jLogger = GetEnv()->NewGlobalRef(jLogger);
}

Logger::~Logger() {
  GetEnv()->DeleteGlobalRef(jLogger);
}

Logger* Logger::create(JNIEnv* env, const char* name) {
  jstring jName = GetEnv()->NewStringUTF(name);
  jobject jLogger = GetEnv()->CallStaticObjectMethod(cls_logger, mid_create, jName);
  //GetEnv()->ReleaseStringUTFChars(jName, name);
  return new Logger(jLogger);
}


void Logger::debug(const char* msg) {
  logNative(msg);
}

void Logger::info(const char* msg) {
  logNative(msg);
}

void Logger::warn(const char* msg) {
  logNative(msg);
}

void Logger::error(const char* msg) {
  logNative(msg);
}

void Logger::logf(const char* level, const char* format, ...) {
  char buf[200];
  va_list vargs;
  va_start(vargs, format);
  char formatBuf[200];
  sprintf(formatBuf, "[%s] %s", level, format);
  vsprintf(buf, formatBuf, vargs);
  va_end(vargs);
  logNative(buf);
}

void Logger::debugf(const char* format, ...) {
  char buf[200];
  va_list vargs;
  va_start(vargs, format);
  char formatBuf[200];
  sprintf(formatBuf, "[DEBUG] %s", format);
  vsprintf(buf, formatBuf, vargs);
  va_end(vargs);
  logNative(buf);
}

void Logger::infof(const char* format, ...) {
  char buf[200];
  va_list vargs;
  va_start(vargs, format);
  char formatBuf[200];
  sprintf(formatBuf, "[INFO ] %s", format);
  vsprintf(buf, formatBuf, vargs);
  va_end(vargs);
  logNative(buf);
}

void Logger::warnf(const char* format, ...) {
  char buf[200];
  va_list vargs;
  va_start(vargs, format);
  char formatBuf[200];
  sprintf(formatBuf, "[WARN ] %s", format);
  vsprintf(buf, formatBuf, vargs);
  va_end(vargs);
  logNative(buf);
}

void Logger::errorf(const char* format, ...) {
  char buf[200];
  va_list vargs;
  va_start(vargs, format);
  char formatBuf[200];
  sprintf(formatBuf, "[ERROR] %s", format);
  vsprintf(buf, formatBuf, vargs);
  va_end(vargs);
  logNative(buf);
}

void Logger::logNative(const char* msg) {
  jstring jMsg = GetEnv()->NewStringUTF(msg);
  GetEnv()->CallVoidMethod(jLogger, mid_logNative, jMsg);
  //GetEnv()->ReleaseStringUTFChars(jMsg, msg);
}

void Logger::initIDs(JNIEnv* env, jclass jClass) {
  cls_logger = (jclass) env->NewGlobalRef(jClass);
  mid_create = env->GetStaticMethodID(jClass, "create", "(Ljava/lang/String;)Lcom/sun/glass/ui/uia/Logger;");
  if (env->ExceptionCheck()) return;
  mid_logNative = env->GetMethodID(jClass, "logNative", "(Ljava/lang/String;)V");
  if (env->ExceptionCheck()) return;
}

extern "C" JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_Logger_initIDs
  (JNIEnv *env, jclass jClass) {
    Logger::initIDs(env, jClass);
}