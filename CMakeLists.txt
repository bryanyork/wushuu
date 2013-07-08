cmake_minimum_required(VERSION 2.8)

project(wushuu)

include(ProcessorCount)
ProcessorCount(PROCESSOR_COUNT)

set(WS_PREFIX ${CMAKE_BINARY_DIR}/target)

include(ExternalProject)
set_directory_properties(PROPERTIES EP_PREFIX ${WS_PREFIX})

if(UNIX)
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
endif()

externalproject_add(opencv
  DEPENDS ffmpeg
  URL http://sourceforge.net/projects/opencvlibrary/files/opencv-unix/2.4.6/opencv-2.4.6.tar.gz
  CMAKE_COMMAND env PKG_CONFIG_PATH=${WS_PREFIX}/lib/pkgconfig cmake
  CMAKE_ARGS -D CMAKE_INSTALL_PREFIX=${WS_PREFIX}
             -D CMAKE_SKIP_RPATH=ON
             -D WITH_1394=OFF
             -D WITH_AVFOUNDATION=OFF
             -D WITH_CARBON=OFF
             -D WITH_CUDA=OFF
             -D WITH_CUBLAS=OFF
             -D WITH_NVCUVID=OFF
             -D WITH_EIGEN=OFF
             -D WITH_VFW=OFF
             -D WITH_FFMPEG=ON
             -D WITH_GSTREAMER=OFF
             -D WITH_GTK=OFF
             -D WITH_IMAGEIO=OFF
             -D WITH_IPP=OFF
             -D WITH_JASPER=OFF
             -D WITH_JPEG=OFF
             -D WITH_OPENEXR=OFF
             -D WITH_OPENNI=OFF
             -D WITH_PNG=OFF
             -D WITH_PVAPI=OFF
             -D WITH_GIGEAPI=OFF
             -D WITH_QT=OFF
             #-D WITH_WIN32UI  using default
             -D WITH_QUICKTIME=OFF
             -D WITH_TBB=OFF
             -D WITH_CSTRIPES=OFF
             -D WITH_TIFF=OFF
             -D WITH_UNICAP=OFF
             -D WITH_V4L=OFF
             #-D WITH_DSHOW    using default
             -D WITH_MSMF=OFF
             -D WITH_XIMEA=OFF
             -D WITH_XINE=OFF
             -D WITH_OPENCL=OFF
             -D WITH_OPENCLAMDFFT=OFF
             -D WITH_OPENCLAMDBLAS=OFF
             -D BUILD_SHARED_LIBS=${UNIX}
             -D BUILD_opencv_apps=ON
             -D BUILD_DOCS=OFF
             -D BUILD_EXAMPLES=OFF
             -D BUILD_PACKAGE=OFF
             -D BUILD_PERF_TESTS=OFF
             -D BUILD_TESTS=OFF
             #-D BUILD_WITH_DEBUG_INFO  using default
             #-D BUILD_WITH_STATIC_CRT  using default
             -D BUILD_opencv_python=OFF
             -D BUILD_opencv_java=OFF
             -D BUILD_opencv_world=OFF
  BUILD_COMMAND env LD_RUN_PATH=\$ORIGIN:\$ORIGIN/../lib make -j${PROCESSOR_COUNT}
  INSTALL_COMMAND make install)

add_subdirectory(src/cpp)

add_custom_target(jvm
  COMMAND cmake -E cmake_echo_color --red --bold "Compiling Java sources ..."
  COMMAND lein javac
  COMMAND cmake -E cmake_echo_color --red --bold "Compiling Clojure sources ..."
  COMMAND lein compile
  DEPENDS wsfd)

add_custom_target(storm
  COMMAND cmake -E cmake_echo_color --red --bold "Making storm topology ..."
  COMMAND lein uberjar
  DEPENDS jvm)
