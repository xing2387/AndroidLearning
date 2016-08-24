#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_xing_androidlearning_natives_JniEntry_getString(JNIEnv *env, jobject instance) {


    return (*env)->NewStringUTF(env, "hello");
}