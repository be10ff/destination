LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := gilib-native
LOCAL_SRC_FILES := gilibraster.cpp
LOCAL_SRC_FILES += gilibvector.cpp
LOCAL_STATIC_LIBRARIES := gdal
LOCAL_STATIC_LIBRARIES += geos
LOCAL_STATIC_LIBRARIES += geos_c
LOCAL_LDLIBS += -llog
LOCAL_LDLIBS += -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
$(call import-module,lib)