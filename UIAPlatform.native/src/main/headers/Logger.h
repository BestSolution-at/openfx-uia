#ifndef _Logger_h_
#define _Logger_h_

#include <jni.h>

class Logger {


  public:
    Logger(jobject jLogger);
    virtual ~Logger();

    static Logger* create(JNIEnv* env, const char* name);

    void debug(const char* msg);
    void info(const char* msg);
    void warn(const char* msg);
    void error(const char* msg);


    void debugf(const char* format, ...);
    void infof(const char* format, ...);
    void warnf(const char* format, ...);
    void errorf(const char* format, ...);

    static void initIDs(JNIEnv* env, jclass cls);

  private:
    

    jobject jLogger;

    void logf(const char* level, const char* format, ...);

    void logNative(const char* msg);

  static jclass cls_logger;
  static jmethodID mid_create;
  static jmethodID mid_logNative;


};

#endif //_Logger_h_
