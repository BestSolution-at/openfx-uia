#include "InstanceTracker.h"

jclass InstanceTracker::cls;
jmethodID InstanceTracker::mid_create;
jmethodID InstanceTracker::mid_destroy;
jmethodID InstanceTracker::mid_setType;
jmethodID InstanceTracker::mid_setReason;
jmethodID InstanceTracker::mid_setJava;
jmethodID InstanceTracker::mid_addRef;
jmethodID InstanceTracker::mid_release;


Logger* InstanceTracker::LOG;

extern "C" JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_InstanceTracker__1initIDs
  (JNIEnv *env, jclass jClass)
{
  InstanceTracker::_init(env, jClass);
}

void InstanceTracker::_init(JNIEnv* env, jclass jClass) {
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

  LOG = Logger::create(env, "native.InstanceTracker");
}

void InstanceTracker::create(void* pointer) {
  GetEnv()->CallVoidMethod(cls, mid_create, (jlong) pointer);
  if (GetEnv()->ExceptionOccurred()) {
    LOG->errorf("InstanceTracker::create failed");
    GetEnv()->ExceptionDescribe();
  }
}

void InstanceTracker::destroy(void* pointer) {
  GetEnv()->CallVoidMethod(cls, mid_destroy, (jlong) pointer);
  if (GetEnv()->ExceptionOccurred()) {
    LOG->errorf("InstanceTracker::destroy failed");
    GetEnv()->ExceptionDescribe();
  }
}

void InstanceTracker::setType(void* pointer, const char* type) {
  jstring jtype = GetEnv()->NewStringUTF(type);
  GetEnv()->CallVoidMethod(cls, mid_setType, (jlong) pointer, jtype);
  // GetEnv()->ReleaseStringUTFChars(jtype, type);
  if (GetEnv()->ExceptionOccurred()) {
    LOG->errorf("InstanceTracker::setType failed");
    GetEnv()->ExceptionDescribe();
  }
}

void InstanceTracker::setReason(void* pointer, const char* reason) {
  jstring jreason = GetEnv()->NewStringUTF(reason);
  GetEnv()->CallVoidMethod(cls, mid_create, (jlong) pointer, jreason);
  // GetEnv()->ReleaseStringUTFChars(jreason, reason);
  if (GetEnv()->ExceptionOccurred()) {
    LOG->errorf("InstanceTracker::setReason failed");
    GetEnv()->ExceptionDescribe();
  }
}

void InstanceTracker::setJava(void* pointer, jobject java) {
  GetEnv()->CallVoidMethod(cls, mid_setJava, (jlong) pointer, java);
  if (GetEnv()->ExceptionOccurred()) {
    LOG->errorf("InstanceTracker::setJava failed");
    GetEnv()->ExceptionDescribe();
  }
}

void InstanceTracker::addRef(void* pointer) {
  GetEnv()->CallVoidMethod(cls, mid_addRef, (jlong) pointer);
  if (GetEnv()->ExceptionOccurred()) {
    LOG->errorf("InstanceTracker::addRef failed");
    GetEnv()->ExceptionDescribe();
  }
}

void InstanceTracker::release(void* pointer) {
  GetEnv()->CallVoidMethod(cls, mid_release, (jlong) pointer);
  if (GetEnv()->ExceptionOccurred()) {
    LOG->errorf("InstanceTracker::release failed");
    GetEnv()->ExceptionDescribe();
  }
}