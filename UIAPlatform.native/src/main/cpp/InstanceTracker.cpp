#include "InstanceTracker.h"

jclass cls;
jmethodID mid_create;
jmethodID mid_destroy;
jmethodID mid_setType;
jmethodID mid_setReason;
jmethodID mid_setJava;
jmethodID mid_addRef;
jmethodID mid_release;

extern "C" JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_InstanceTracker__1initIDs
  (JNIEnv *env, jclass jClass)
{
  cls = (jclass) env->NewGlobalRef(jClass);

  mid_create = env->GetStaticMethodID(jClass, "create", "(J)V");
  if (env->ExceptionCheck()) return;
  mid_destroy = env->GetStaticMethodID(jClass, "destroy", "(J)V");
  if (env->ExceptionCheck()) return;
  mid_setType = env->GetStaticMethodID(jClass, "setType", "(JLjava/lang/String;)V");
  if (env->ExceptionCheck()) return;
  mid_setReason = env->GetStaticMethodID(jClass, "setReason", "(JLjava/lang/String;)V");
  if (env->ExceptionCheck()) return;
    mid_setJava = env->GetStaticMethodID(jClass, "setJava", "(JLjava/lang/Object;)V");
  if (env->ExceptionCheck()) return;
  mid_addRef = env->GetStaticMethodID(jClass, "addRef", "(J)V");
  if (env->ExceptionCheck()) return;
  mid_release = env->GetStaticMethodID(jClass, "release", "(J)V");
  if (env->ExceptionCheck()) return;
}

void InstanceTracker::create(void* pointer) {
  GetEnv()->CallVoidMethod(cls, mid_create, (jlong) pointer);
}

void InstanceTracker::destroy(void* pointer) {
  GetEnv()->CallVoidMethod(cls, mid_destroy, (jlong) pointer);
}

void InstanceTracker::setType(void* pointer, const char* type) {
  jstring jtype = GetEnv()->NewStringUTF(type);
  GetEnv()->CallVoidMethod(cls, mid_setType, (jlong) pointer, jtype);
  // GetEnv()->ReleaseStringUTFChars(jtype, type);
}

void InstanceTracker::setReason(void* pointer, const char* reason) {
  jstring jreason = GetEnv()->NewStringUTF(reason);
  GetEnv()->CallVoidMethod(cls, mid_create, (jlong) pointer, jreason);
  // GetEnv()->ReleaseStringUTFChars(jreason, reason);
}

void InstanceTracker::setJava(void* pointer, jobject java) {
  GetEnv()->CallVoidMethod(cls, mid_setJava, (jlong) pointer, java);
}

void InstanceTracker::addRef(void* pointer) {
  GetEnv()->CallVoidMethod(cls, mid_addRef, (jlong) pointer);
}

void InstanceTracker::release(void* pointer) {
  GetEnv()->CallVoidMethod(cls, mid_release, (jlong) pointer);
}