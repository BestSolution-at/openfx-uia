#ifndef _INSTANCE_TRACKER_
#define _INSTANCE_TRACKER_

#include "common.h"

class InstanceTracker {

public:

  static void _init();

  static void create(void* pointer);
  static void destroy(void *pointer);
  static void setType(void* pointer, const char* type);
  static void setReason(void* pointer, const char* reason);
  static void setJava(void* pointer, jobject java);
  static void addRef(void* pointer);
  static void release(void* pointer);

};

#endif //_INSTANCE_TRACKER_
