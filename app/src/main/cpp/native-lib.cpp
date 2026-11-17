#include <jni.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_pinkstation4vr_MainActivity_nativeStatus(JNIEnv* env, jobject) {
    return env->NewStringUTF("PinkStation native runtime loaded (ARM64)");
}
