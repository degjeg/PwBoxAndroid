//
// Created by danger on 16/9/18.
//
#include "jni.h"
#include "stdio.h"
#include "includes/Log.h"
#include "jni_tool.h"
#include "constants.h"
#include "encrypt.h"
// #include "../../../../../../Android/sdk/android-ndk-r10e/platforms/android-21/arch-x86/usr/include/string.h"
#include "string.h"

#ifdef __cplusplus
extern "C" {
#endif


char rawk1[]={77, -88, 119, 38, -81, -59, -102, -70, 119, -65, 36, -77, -69, 30, -55, -16};
char rawk2[]={36, -97, 35, -20, -65, 9, -83, 50, 9, -124, -97, 106, -77, -94, -78, -103};
char rawm1[]={11, 57, 90, 85, -23, 85, -83, 12, 12, -11, -113, 68, -124, 17, -25, -123, -30, -41, -47, 94, -69, -55, 6, 4, 49, 68, -63, -65, -25, -51, -9, 25, 117, -79, 14, 110, 49, 117, 115, 15, -86, 73, 107, -84, 79, 112, -52, 4, -58, 82, 102, 52, 49, -12, 97, -61, -114, -17, -124, -30, -9, 93, -19, -40, 14, 122, -6, -54, 60, -75, 83, 58, -37, -42, -59, 120, -53, -8, 14, -115, 120, 22, 127, -98, -40, -85, 23, 2, 89, 44, -62, -83, 35, 39, 90, -60, 58, 71, -103, -48, 88, 119, 0, -10, -24, 24, 64, -78, -75, -67, 65, 38, -33, 73, -77, 23, 98, 58, -32, -48, 23, 54, -108, -23, -75, 0, 72, 60, -90, 122, -51, 70, 71, -12, -80, -5, 24, 85, -53, -105, 112, 13, 55, 58, -121, -78, -10, 63, -74, -102, 19, 63, -98, -28, 15, -9, 112, -39, -121, 100, 67, 54, 44, 56, -49, -3, -13, 5, 0, -67, 38, -18, 1, -74, -108, -17, 6, -35, -105, -51, 115, 82, -28, -23, -7, 95, 90, -78, -5, 69, -90, 46, -33, 122, 78, 83, -109, -25, 54, 59, -76, -15, -85, -16, -79, 68, 62, 7, -107, 110, -67, -45, -120, 117, 92, -97, 50, -79, 41, -23, -113, 5, -83, 19, -17, -92, -4, 67, 32, 88, 103, 66, -125, -59, 92, -116, 67, 103, -125, 125, 71, 103, 2, -63, -26, 30, -76, 10, 117, -74, 41, -123, -20, 24, 25, 126, 11, 56, 82, 83, -88, 64, -85, 78, 88, -23, -63, 92, -113, 31, -27, -126, -22, -44, -39, 91, -75, -50, 1, 8, 52, 77, -118, -81, -28, -59, -1, 16, 48, -96, 71, 126, 53, 113, 121, 74, -75, 64, 41, -70, 14, 111, -114, 26, -58, 85, 97, 55};
char rawp1[]={0, 54, -96, -35, -86};


int len1() {
    return sizeof(rawk1);
}

int len2() {
    return sizeof(rawk2);
}

int len3() {
    return sizeof(rawm1);
}

int len4() {
    return sizeof(rawp1);
}

jobject getApplication(JNIEnv *env) {
    jclass localClass = env->FindClass(android_app_ActivityThread_);/*"android/app/ActivityThread"*/
    if (localClass != NULL) {

        jmethodID getapplication = env->GetStaticMethodID(
                localClass, currentApplication_ /*"currentApplication"*/,
                __Landroid_app_Application__/*"()Landroid/app/Application;"*/);
        if (getapplication != NULL) {
            jobject application = env->CallStaticObjectMethod(localClass, getapplication);
            return application;
        }
        return NULL;
    }
    return NULL;
}

void decStep(char *d, int len, const char *k, int key) {
    decrypt(d, len, key);

    for (int i = 0; i < len; i++) {
        d[i] = d[i] ^ k[i];
    }
}

void verifySign(JNIEnv *env, jobject obj) {
    jobject context = getApplication(env);
    jclass activity = env->GetObjectClass(context);
    // 得到 getPackageManager 方法的 ID
    jmethodID methodID_func = env->GetMethodID(
            activity, getPackageManager_ /*"getPackageManager"*/,
            __Landroid_content_pm_PackageManager__  /*"()Landroid/content/pm/PackageManager;"*/);
    // 获得PackageManager对象
    jobject packageManager = env->CallObjectMethod(context, methodID_func);
    jclass packageManagerclass = env->GetObjectClass(packageManager);
    //得到 getPackageName 方法的 ID
    jmethodID methodID_pack = env->GetMethodID(
            activity, getPackageName_/*"getPackageName"*/,
            __Ljava_lang_String__/*"()Ljava/lang/String;"*/);
    //获取包名
    jstring name_str = static_cast<jstring>(env->CallObjectMethod(context, methodID_pack));
    // 得到 getPackageInfo 方法的 ID
    jmethodID methodID_pm = env->GetMethodID(
            packageManagerclass, getPackageInfo_/*"getPackageInfo"*/,
            _Ljava_lang_String_I_Landroid_content_pm_PackageInfo__ /*"(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;"*/);
    // 获得应用包的信息
    jobject package_info = env->CallObjectMethod(packageManager, methodID_pm, name_str, 64);
    // 获得 PackageInfo 类
    jclass package_infoclass = env->GetObjectClass(package_info);
    // 获得签名数组属性的 ID
    jfieldID fieldID_signatures = env->GetFieldID(
            package_infoclass, signatures_/*"signatures"*/,
            _Landroid_content_pm_Signature__/*"[Landroid/content/pm/Signature;"*/);
    // 得到签名数组，待修改
    jobject signatur = env->GetObjectField(package_info, fieldID_signatures);
    jobjectArray signatures = reinterpret_cast<jobjectArray>(signatur);
    // 得到签名
    jobject signature = env->GetObjectArrayElement(signatures, 0);
    // 获得 Signature 类，待修改
    jclass signature_clazz = env->GetObjectClass(signature);
    //获取sign
    jmethodID toCharString = env->GetMethodID(
            signature_clazz, toCharsString_/*"toCharsString"*/,
            __Ljava_lang_String__  /*"()Ljava/lang/String;"*/);

    //获取签名字符；或者其他进行验证操作
    jstring signstr = static_cast<jstring>(env->CallObjectMethod(signature, toCharString));
    const char *ch = getJStringUtfChars(env, signstr);
    //输入签名字符串，这里可以进行相关验证

    decStep(rawk1, sizeof(rawk1), ch, 11111);
    decStep(rawk2, sizeof(rawk2), ch, 22222);
    decStep(rawm1, sizeof(rawm1), ch, 33333);
    decStep(rawp1, sizeof(rawp1), ch, 44444);
}


#ifdef __cplusplus
}
#endif
