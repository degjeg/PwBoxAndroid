//
// Created by danger on 16/9/18.
//

#ifndef PWBOX_JNI_TOOL_H_H
#define PWBOX_JNI_TOOL_H_H


#include "jni.h"

const char *getJStringUtfChars(JNIEnv *env, jstring jstr);

void releaseJStringUtfChars(JNIEnv *env, jstring jstr, const char *utfChars);

//C字符串转java字符串
jstring strToJstring(JNIEnv *env, const char *pStr);

jbyteArray strToJByteArray(JNIEnv *env, const char *pStr, int len);
// jstring strToJByteArrsyWithLen(JNIEnv *env, const char *pStr, int len);

int registerNativeMethods(
        JNIEnv *env, const char *className, JNINativeMethod *gMethods,
        int numMethods);

#endif //PWBOX_JNI_TOOL_H_H
