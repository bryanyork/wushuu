cmake_minimum_required(VERSION 2.8)

project(wushuu_lib)

set(CMAKE_FIND_ROOT_PATH ${WS_PREFIX})

find_package(OpenCV REQUIRED ONLY_CMAKE_FIND_ROOT_PATH)
find_package(PkgConfig REQUIRED)
pkg_check_modules(FFMPEG REQUIRED libavformat libavcodec libavutil libswscale)

include_directories(BEFORE ${WS_PREFIX}/include) 
link_directories(${WS_PREFIX}/lib)

set(OPENCV_LIBS opencv_core opencv_imgproc opencv_objdetect opencv_highgui)
set(FFMPEG_LIBS avformat avcodec avutil)

add_library(wsfd SHARED facedetect.cpp)
target_link_libraries(wsfd ${OpenCV_LIBS} ${FFMPEG_LDFLAGS})
set_target_properties(wsfd PROPERTIES
  VERSION 1.0
  SOVERSION 1
  LINK_FLAGS "-Wl,--as-needed"
  INSTALL_RPATH "\$ORIGIN")

add_executable(fd facedetect.cpp)
target_link_libraries(fd ${OpenCV_LIBS} ${FFMPEG_LDFLAGS})
set_target_properties(fd PROPERTIES
  LINK_FLAGS "-Wl,--as-needed"
  INSTALL_RPATH "\$ORIGIN/../lib")

#add_custom_command(TARGET wsfd POST_BUILD
#  COMMAND cmake -E cmake_echo_color --red --bold "Creating wsnatives.jar ..."
#  COMMAND jar ARGS cfM ${CMAKE_BINARY_DIR}/res/native/wsnatives.jar @${WS_PREFIX}/wsnatives.list
#  COMMAND jar ARGS ufM ${CMAKE_BINARY_DIR}/res/native/wsnatives.jar $<TARGET_LINKER_FILE_NAME:wsfd>
#  WORKING_DIRECTORY ${WS_PREFIX}/lib)

install(TARGETS wsfd fd
  RUNTIME DESTINATION bin
  LIBRARY DESTINATION lib)
