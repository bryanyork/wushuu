cmake_minimum_required(VERSION 2.8)

project(wushuu)

include(ProcessorCount)
ProcessorCount(PROCESSOR_COUNT)

set(WS_PREFIX ${CMAKE_BINARY_DIR}/target)

include(ExternalProject)
set_directory_properties(PROPERTIES EP_PREFIX ${WS_PREFIX})

externalproject_add(ffmpeg
  URL http://www.ffmpeg.org/releases/ffmpeg-1.2.1.tar.bz2
  CONFIGURE_COMMAND <SOURCE_DIR>/configure --prefix=${WS_PREFIX}
                                           --enable-gpl
                                           --enable-nonfree
                                           --enable-version3
                                           --disable-encoders
                                           --disable-outdevs
                                           --disable-doc
                                           --disable-static
                                           --enable-shared
                                           --enable-pic
                                           --enable-debug
                                           --disable-stripping
  BUILD_COMMAND env LD_RUN_PATH=\$ORIGIN:\$ORIGIN/../lib make -j${PROCESSOR_COUNT})

externalproject_add(opencv
  DEPENDS ffmpeg
  URL http://sourceforge.net/projects/opencvlibrary/files/opencv-unix/2.4.6/opencv-2.4.6.tar.gz
  CMAKE_COMMAND env PKG_CONFIG_PATH=${WS_PREFIX}/lib/pkgconfig cmake
  CMAKE_ARGS -D CMAKE_INSTALL_PREFIX=${WS_PREFIX}
             -D CMAKE_INSTALL_RPATH="\$ORIGIN"
             -D BUILD_DOCS=OFF
             -D BUILD_PERF_TESTS=OFF
             -D BUILD_TESTS=OFF
             -D BUILD_opencv_python=OFF
             -D BUILD_opencv_java=OFF
             -D BUILD_opencv_world=ON
             -D BUILD_EXAMPLES=OFF
	     -D BUILD_opencv_apps=ON
  BUILD_COMMAND env LD_RUN_PATH=\$ORIGIN:\$ORIGIN/../lib make -j${PROCESSOR_COUNT})

add_subdirectory(src/cpp)

add_custom_command(OUTPUT target/wsnatives.list
  COMMAND ldd target/lib/libwsfd.so | fgrep target | cut -d " " -f 3 | sed -e "s#.*/##" > target/wsnatives.list)
add_custom_command(OUTPUT wsnatives.jar
  COMMAND jar ARGS cfM ../../resource/native/wsnatives.jar @../../target/wsnatives.list
  COMMAND jar ARGS ufM ../../resource/native/wsnatives.jar libwsfd.so
  WORKING_DIRECTORY target/lib
  DEPENDS target/wsnatives.list)
add_custom_target(wsnatives DEPENDS wsnatives.jar)
