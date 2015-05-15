LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := gilib-native
LOCAL_SRC_FILES := \
	/home/a_belov/AndroidProjects/AndroidApplication/gILib/src/main/jni/Android.mk \
	/home/a_belov/AndroidProjects/AndroidApplication/gILib/src/main/jni/gilibraster.cpp \
	/home/a_belov/AndroidProjects/AndroidApplication/gILib/src/main/jni/Application.mk \
	/home/a_belov/AndroidProjects/AndroidApplication/gILib/src/main/jni/gilibvector.cpp \

LOCAL_C_INCLUDES += /home/a_belov/AndroidProjects/AndroidApplication/gILib/src/main/jni
LOCAL_C_INCLUDES += /home/a_belov/AndroidProjects/AndroidApplication/gILib/src/release/jni

include $(BUILD_SHARED_LIBRARY)
