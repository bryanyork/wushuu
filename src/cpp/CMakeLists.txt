cmake_minimum_required(VERSION 2.8)

project(wushuu-native)

set(CMAKE_FIND_ROOT_PATH ${WS_PREFIX})

find_package(PkgConfig REQUIRED)
pkg_check_modules(FFmpeg REQUIRED libavformat libavcodec libavutil libswscale)
pkg_check_modules(X264 REQUIRED x264)
find_package(OpenCV REQUIRED ONLY_CMAKE_FIND_ROOT_PATH)

set(Boost_USE_STATIC_LIBS ON)
find_package(Boost 1.54 EXACT REQUIRED
  COMPONENTS thread)

include_directories(BEFORE ${X264_INCLUDE_DIRS} ${FFmpeg_INCLUDE_DIRS} ${OpenCV_INCLUDE_DIRS} ${Boost_INCLUDE_DIRS}) 
link_directories(${X264_LIBRARY_DIRS} ${FFmpeg_LIBRARY_DIRS} ${OpenCV_LIB_DIR} ${Boost_LIBRARY_DIRS})

add_library(wsnative SHARED facedetect.cpp bgfg_codebook.cpp blobanalyzer.cpp)
target_link_libraries(wsnative ${OpenCV_LIBS} ${FFmpeg_LIBRARIES} ${X264_LIBRARIES} ${Boost_LIBRARIES})
set_target_properties(wsnative PROPERTIES
  VERSION 1.0
  SOVERSION 1
  LINK_FLAGS "-Wl,--as-needed -Wl,-Bsymbolic"
  INSTALL_RPATH "\$ORIGIN")

install(TARGETS wsnative
  LIBRARY DESTINATION lib)

