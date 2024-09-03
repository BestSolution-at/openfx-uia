#pragma once

#include "common.h"
#include "Logger.h"

class InstanceTracker {

private:
  InstanceTracker();
  ~InstanceTracker();
public:
  InstanceTracker(const InstanceTracker&) = delete;
  InstanceTracker& operator =(const InstanceTracker&) = delete;

  static auto& Get() {
    static InstanceTracker instance;
    return instance;
  }

public:
  void create(void* pointer);
  void destroy(void *pointer);
  void setType(void* pointer, const char* type);
  void setReason(void* pointer, const char* reason);
  void setJava(void* pointer, jobject java);
  void addRef(void* pointer);
  void release(void* pointer);

  void debug(void* pointer);

  static void _init(JNIEnv* env, jclass cls);

private:
  Logger m_log;

  static jclass cls;
  static jmethodID mid_create;
  static jmethodID mid_destroy;
  static jmethodID mid_setType;
  static jmethodID mid_setReason;
  static jmethodID mid_setJava;
  static jmethodID mid_addRef;
  static jmethodID mid_release;
  static jmethodID mid_debug;

};
