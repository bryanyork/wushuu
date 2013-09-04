cmake_minimum_required(VERSION 2.8)

project(wushuu-native)

set(CMAKE_FIND_ROOT_PATH ${WS_PREFIX})

if(UNIX)
  find_package(PkgConfig REQUIRED)
  pkg_check_modules(FFmpeg REQUIRED libavformat libavcodec libavutil libswscale)
  pkg_check_modules(X264 REQUIRED x264)
endif()
find_package(OpenCV REQUIRED ONLY_CMAKE_FIND_ROOT_PATH)

set(Boost_USE_STATIC_LIBS ON)
set(Boost_USE_STATIC_RUNTIME ON)
find_package(Boost 1.54 EXACT REQUIRED
  COMPONENTS thread system)

include_directories(BEFORE ${X264_INCLUDE_DIRS} ${FFmpeg_INCLUDE_DIRS} ${OpenCV_INCLUDE_DIRS} ${Boost_INCLUDE_DIRS}) 
link_directories(${X264_LIBRARY_DIRS} ${FFmpeg_LIBRARY_DIRS} ${OpenCV_LIB_DIR} ${Boost_LIBRARY_DIRS})

if(WIN32)
  set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} /MTd")
  set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_DEBUG} /MT")
endif()

add_library(wsnative SHARED facedetect.cpp bgfg_codebook.cpp blobanalyzer.cpp)
target_link_libraries(wsnative ${OpenCV_LIBS} ${FFmpeg_LIBRARIES} ${X264_LIBRARIES} ${Boost_LIBRARIES})

include(GenerateExportHeader)
GENERATE_EXPORT_HEADER(wsnative)
ADD_COMPILER_EXPORT_FLAGS()

set_target_properties(wsnative PROPERTIES
  VERSION 1.0
  SOVERSION 1
  COMPILE_FLAGS "-I${CMAKE_CURRENT_BINARY_DIR}"  # for export header
  LINK_FLAGS "-Wl,--as-needed -Wl,-Bsymbolic"
  INSTALL_RPATH "\$ORIGIN")

install(TARGETS wsnative
  ARCHIVE DESTINATION lib
  LIBRARY DESTINATION lib
  RUNTIME DESTINATION bin)

