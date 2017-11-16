LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include D:\OneDrive\PRO\Android\OpenCV-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_MODULE    := NativeBlend
LOCAL_SRC_FILES := NativeBlend.cpp
LOCAL_LDFLAGS := -llog

include $(BUILD_SHARED_LIBRARY)