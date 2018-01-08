LOCAL_PATH := $(call my-dir)

# cmd-strip = $(TOOLCHAIN_PREFIX)strip --strip-debug -x $1

include $(CLEAR_VARS)

LOCAL_MODULE    := core
LOCAL_CFLAGS := -DANDROID_NDK \
                -DDISABLE_IMPORTGL \
                -fvisibility=hidden


LOCAL_SRC_FILES := check.cpp constants.c encrypt.cpp jni_tool.cpp	main.cpp


LOCAL_LDLIBS    += -fPIC -llog
#LOCAL_LDLIBS += libjpeg.so
include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_EXECUTABLE)
