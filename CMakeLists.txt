cmake_minimum_required(VERSION 2.8)

project(wushuu)

include(FindPkgConfig)

find_package(OpenCV 2.4.5 REQUIRED core imgproc objdetect highgui PATHS 3rdparty NO_DEFAULT_PATH)

add_subdirectory(src/cpp)

add_custom_command(OUTPUT target/wsnatives.list
  COMMAND ldd 3rdparty/lib/libwsfd.so | fgrep 3rdparty | cut -d " " -f 3 | sed -e "s#.*/##" > target/wsnatives.list)
add_custom_command(OUTPUT wsnatives.jar
  COMMAND jar ARGS cfM ../../resource/native/wsnatives.jar @../../target/wsnatives.list
  WORKING_DIRECTORY 3rdparty/lib
  DEPENDS target/wsnatives.list)
add_custom_target(wsnatives DEPENDS wsnatives.jar)
