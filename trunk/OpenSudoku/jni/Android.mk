LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := sudogen
LOCAL_SRC_FILES := sudo.c

include $(BUILD_SHARED_LIBRARY)
