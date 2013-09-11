cmake_minimum_required(VERSION 2.8)

project(wushuu-native-test)

set(CMAKE_FIND_ROOT_PATH ${WS_PREFIX})

include(${WS_PREFIX}/lib/wsnative.cmake)

if(UNIX)
  find_package(PkgConfig REQUIRED)
  pkg_check_modules(FFmpeg REQUIRED libavformat libavcodec libavutil libswscale)
  pkg_check_modules(X264 REQUIRED x264)
endif()
find_package(OpenCV REQUIRED ONLY_CMAKE_FIND_ROOT_PATH)

include_directories(BEFORE ${X264_INCLUDE_DIRS} ${FFmpeg_INCLUDE_DIRS} ${OpenCV_INCLUDE_DIRS})
link_directories(${X264_LIBRARY_DIRS} ${FFmpeg_LIBRARY_DIRS} ${OpenCV_LIB_DIR})

if(WIN32)
  set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} /MTd")
  set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_DEBUG} /MT")
endif()

add_library(wsnativetest SHARED test.cpp)
target_link_libraries(wsnativetest ${OpenCV_LIBS} ${FFmpeg_LIBRARIES} ${X264_LIBRARIES})
set_target_properties(wsnativetest PROPERTIES
  VERSION 1.0
  SOVERSION 1
  LINK_FLAGS "-Wl,--as-needed -Wl,-Bsymbolic"
  INSTALL_RPATH "\$ORIGIN")

add_executable(wsnt test.cpp)
target_link_libraries(wsnt ${OpenCV_LIBS} ${FFmpeg_LIBRARIES} ${X264_LIBRARIES})
set_target_properties(wsnt PROPERTIES
  LINK_FLAGS "-Wl,--as-needed"
  INSTALL_RPATH "\$ORIGIN/../lib")

add_executable(test_blobanalyzer test_blobanalyzer.cpp)
target_link_libraries(test_blobanalyzer ${OpenCV_LIBS} ${FFmpeg_LIBRARIES} ${X264_LIBRARIES} wsnative)
set_target_properties(test_blobanalyzer PROPERTIES
  LINK_FLAGS "-Wl,--as-needed"
  INSTALL_RPATH "\$ORIGIN/../lib")

install(TARGETS wsnativetest wsnt test_blobanalyzer
  RUNTIME DESTINATION bin
  LIBRARY DESTINATION lib)