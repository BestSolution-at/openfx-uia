#pragma once

#include <jni.h>
#include <mutex>
#include <utility>

// A COM provider may outlive its Java peer. Callbacks borrow a local reference;
// disposal drops ownership without invalidating references already borrowed.
class JavaPeer {
public:
    JavaPeer(JNIEnv* env, jobject peer) : global(env->NewGlobalRef(peer)) {}
    JavaPeer(const JavaPeer&) = delete;
    JavaPeer& operator=(const JavaPeer&) = delete;

    // The caller must use a JNI local frame (or explicitly delete the result).
    jobject acquire(JNIEnv* env) {
        std::lock_guard<std::mutex> lock(mutex);
        return global ? env->NewLocalRef(global) : nullptr;
    }

    // Idempotent; never holds the mutex while calling back into Java code.
    bool dispose(JNIEnv* env) {
        jobject previous;
        {
            std::lock_guard<std::mutex> lock(mutex);
            previous = std::exchange(global, nullptr);
        }
        if (!previous) return false;
        env->DeleteGlobalRef(previous);
        return true;
    }

private:
    std::mutex mutex;
    jobject global;
};
