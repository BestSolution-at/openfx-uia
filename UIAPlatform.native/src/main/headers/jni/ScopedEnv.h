#pragma once

#include <jni.h>

namespace uia::jni {
// COM's final Release can run on a thread not owned by Java.
class ScopedEnv {
public:
    explicit ScopedEnv(JavaVM* vm) : vm(vm) {
        if (!vm) return;
        jint result = vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_2);
        if (result == JNI_EDETACHED) {
            attached = vm->AttachCurrentThreadAsDaemon(reinterpret_cast<void**>(&env), nullptr) == JNI_OK;
            if (!attached) env = nullptr;
        } else if (result != JNI_OK) {
            env = nullptr;
        }
    }
    ~ScopedEnv() { if (attached) vm->DetachCurrentThread(); }
    ScopedEnv(const ScopedEnv&) = delete;
    ScopedEnv& operator=(const ScopedEnv&) = delete;

    JNIEnv* env = nullptr;
private:
    JavaVM* vm;
    bool attached = false;
};
}
