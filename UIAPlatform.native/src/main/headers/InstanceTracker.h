#ifndef _INSTANCE_TRACKER_
#define _INSTANCE_TRACKER_

#include "common.h"
#include "Logger.h"

class InstanceTracker {

public:

  static void _init(JNIEnv* env, jclass cls);

  static void create(void* pointer);
  static void destroy(void *pointer);
  static void setType(void* pointer, const char* type);
  static void setReason(void* pointer, const char* reason);
  static void setJava(void* pointer, jobject java);
  static void addRef(void* pointer);
  static void release(void* pointer);

private:
  static Logger* LOG;

  static jclass cls;
  static jmethodID mid_create;
  static jmethodID mid_destroy;
  static jmethodID mid_setType;
  static jmethodID mid_setReason;
  static jmethodID mid_setJava;
  static jmethodID mid_addRef;
  static jmethodID mid_release;
};

#endif //_INSTANCE_TRACKER_
