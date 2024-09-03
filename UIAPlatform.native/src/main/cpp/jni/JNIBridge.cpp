
#include "jni/JNIBridge.h"

#include <iostream>

#include <thread>

namespace uia::jni {


JavaVM* JNIBridge::m_vm;

JNIBridge::JNIBridge(): m_log("JNIBridge") {
}
JNIBridge::~JNIBridge() {
}

jint JNIBridge::OnLoad(JavaVM* vm) {
  JNIBridge::m_vm = vm;
  return JNI_VERSION_1_2;
}

void JNIBridge::OnUnload(JavaVM* vm) {
  // TODO
}

JavaVM* JNIBridge::GetVM() {
  return m_vm;
}

JNIEnv* JNIBridge::GetEnv() {
  JNIEnv* env;
  m_vm->GetEnv((void**) &env, JNI_VERSION_1_2);
  return env;
}

jclass JNIBridge::FindClass(std::string_view name) {
  auto result = GetEnv()->FindClass(std::string(name).c_str());
  // TODO sanity checks
  return result;
}

jmethodID JNIBridge::GetStaticMethodID(jclass cls, const char* name, const char* sig) {
  auto env = GetEnv();
  auto result = env->GetStaticMethodID(cls, name, sig);
  // TODO sanity checks
  if (result == nullptr) {
    m_log.error() << "GetStaticMethodID returned nullptr (" << name << ", "<< sig << ")" << std::endl;
  }
  if (env->ExceptionOccurred()) {
    m_log.error() << "GetStaticMethodID exception (" << name << ", "<< sig << ")" << std::endl;
    env->ExceptionDescribe();
  }
  return result;
}

LocalRef<jstring> JNIBridge::NewStringUTF(const char* utf) {
  return LocalRef<jstring>(*this, GetEnv()->NewStringUTF(utf));
}

}

extern "C" {
  JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void* reserved) {
  return uia::jni::JNIBridge::OnLoad(vm);
  }
  JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void* reserved) {
    uia::jni::JNIBridge::OnUnload(vm);
  }
}
