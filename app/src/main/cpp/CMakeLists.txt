
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("qrcodemaker")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.
include_directories(include lib util)
add_library(${CMAKE_PROJECT_NAME} SHARED
        qrcodemaker.cpp
        util/jni-util.cpp
        lib/qrcode/content/app-market.cpp
        lib/qrcode/content/biz-card.cpp
        lib/qrcode/content/calendar.cpp
        lib/qrcode/content/email.cpp
        lib/qrcode/content/face-time.cpp
        lib/qrcode/content/geo.cpp
        lib/qrcode/content/me-card.cpp
        lib/qrcode/content/phone-call.cpp
        lib/qrcode/content/sms.cpp
        lib/qrcode/content/v-card.cpp
        lib/qrcode/content/wifi.cpp
        lib/qrcode/qrcode-util.cpp
        lib/qrcode/qrcodegen.cpp
        lib/util/string-util.cpp
        )

#include_directories(lib)
#add_library(${CMAKE_PROJECT_NAME} SHARED
#    # List C/C++ source files with relative paths to this CMakeLists.txt.
#    qrcodemaker.cpp
#        )

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        log
        )
