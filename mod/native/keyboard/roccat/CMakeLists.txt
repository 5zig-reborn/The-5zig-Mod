cmake_minimum_required(VERSION 3.6)
project(Roccat)

find_package(JNI)

if (JNI_FOUND)
    message (WARNING "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
    message (WARNING "JNI_LIBRARIES=${JNI_LIBRARIES}")
else()
    message (WARNING "JNI NOT FOUND")
endif()

add_definitions(-DWIN32 -D_WINDOWS -D_UNICODE -DUNICODE)

add_library(roccat.dll SHARED roccat.cpp)
include_directories("C:/Program Files/Java/jdk1.6.0_45/include")
include_directories("C:/Program Files/Java/jdk1.6.0_45/include/win32")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES roccat.cpp)
add_executable(Roccat ${SOURCE_FILES})