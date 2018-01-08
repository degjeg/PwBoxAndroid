//
// Created by danger on 16/9/18.
//
#include "jni_tool.h"
#include "stdio.h"
#include "string.h"

const char *getJStringUtfChars(JNIEnv *env, jstring jstr) {
    return env->GetStringUTFChars(jstr, 0);
    // char *rtn = NULL;
    // jclass clsstring = env->FindClass("java/lang/String");
    // jstring strencode = env->NewStringUTF("utf-8");
    // jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    // jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    // jsize alen = env->GetArrayLength(barr);
    // jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    // if (alen > 0) {
    //     rtn = (char *) malloc(alen + 1);
    //     memcpy(rtn, ba, alen);
    //     rtn[alen] = 0;
    // }
    // env->ReleaseByteArrayElements(barr, ba, 0);
    // return rtn;
}


void releaseJStringUtfChars(JNIEnv *env, jstring jstr, const char *utfChars) {
    env->ReleaseStringUTFChars(jstr, utfChars);
}

//C字符串转java字符串
jstring strToJstring(JNIEnv *env, const char *pStr) {
    int strLen = strlen(pStr);
    jclass jstrObj = env->FindClass("java/lang/String");
    jmethodID methodId = env->GetMethodID(jstrObj, "<init>", "([BLjava/lang/String;)V");
    jbyteArray byteArray = env->NewByteArray(strLen);
    jstring encode = env->NewStringUTF("utf-8");

    env->SetByteArrayRegion(byteArray, 0, strLen, (jbyte *) pStr);

    return (jstring) env->NewObject(jstrObj, methodId, byteArray, encode);
}

jbyteArray strToJByteArray(JNIEnv *env, const char *pStr, int len) {
    // int strLen = strlen(pStr);
    jclass jstrObj = env->FindClass("java/lang/String");
    jmethodID methodId = env->GetMethodID(jstrObj, "<init>", "([BLjava/lang/String;)V");
    jbyteArray byteArray = env->NewByteArray(len);

    env->SetByteArrayRegion(byteArray, 0, len, (jbyte *) pStr);
    return byteArray;
}
int registerNativeMethods(
        JNIEnv *env, const char *className, JNINativeMethod *gMethods,
        int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

