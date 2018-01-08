//
// Created by danger on 16/9/18.
//

#ifndef PWBOX_LOG_H
#define PWBOX_LOG_H

/* Header for class com_mll_imagetool_ImageTool */
#include <jni.h>
#include <android/log.h>

#define TAG "jni_"

#define L 1

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
// 定义debug信息
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
// 定义error信息
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)


#endif //PWBOX_LOG_H
