#pragma once

#include <string>
#include <iostream>

#include <jni.h>

#include "Logger.h"

namespace uia::jni {


class JNIBridge;

template<typename T>
class LocalRef {
  private:
  JNIBridge& m_bridge;
  T m_obj;

  public:
  LocalRef(const LocalRef&) = delete;
  LocalRef& operator=(const LocalRef&) = delete;

  LocalRef(JNIBridge& bridge, T obj): m_bridge(bridge), m_obj(obj) {
  }
  ~LocalRef() {
    m_bridge.GetEnv()->DeleteLocalRef(m_obj);
  }

  T Get() {
    return m_obj;
  }

};

class JNIBridge {

private:
  JNIBridge();
  ~JNIBridge();

  Logger m_log;

public:
  JNIBridge(const JNIBridge&) = delete;
  JNIBridge& operator=(const JNIBridge &) = delete;
  JNIBridge(JNIBridge &&) = delete;
  JNIBridge & operator=(JNIBridge &&) = delete;

  static auto& Get(){
    static JNIBridge instance;
    return instance;
  }

 public:

  static jint OnLoad(JavaVM* vm);
  static void OnUnload(JavaVM* vm);

  // temp
  static JavaVM* GetVM();
  static JNIEnv* GetEnv();

  jclass FindClass(std::string_view name);
  jmethodID GetStaticMethodID(jclass cls, const char* name, const char* sig);

  LocalRef<jstring> NewStringUTF(const char* utf);

;


private:

  static JavaVM* m_vm;
};


}
