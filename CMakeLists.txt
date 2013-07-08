cmake_minimum_required(VERSION 2.8)

project(wushuu)

include(ProcessorCount)
ProcessorCount(PROCESSOR_COUNT)

set(WS_PREFIX ${CMAKE_BINARY_DIR}/target)

include(ExternalProject)
set_directory_properties(PROPERTIES EP_PREFIX ${WS_PREFIX})

if(UNIX)
  externalproject_add(pkg-config
    URL http://pkgconfig.freedesktop.org/releases/pkg-config-0.28.tar.gz
    CONFIGURE_COMMAND <SOURCE_DIR>/configure --prefix=${WS_PREFIX})

  externalproject_add(ffmpeg
    URL http://www.ffmpeg.org/releases/ffmpeg-1.2.1.tar.bz2
    CONFIGURE_COMMAND CFLAGS=-fvisibility=hidden <SOURCE_DIR>/configure
                                             --prefix=${WS_PREFIX}
                                             --enable-gpl
                                             --enable-nonfree
                                             --enable-version3
                                             --disable-devices
                                             --disable-doc
                                             --disable-shared
                                             --enable-pic
                                             --enable-static
                                             --enable-debug
                                             --disable-stripping
    BUILD_COMMAND make -j${PROCESSOR_COUNT})
endif()

externalproject_add(opencv
  DEPENDS pkg-config ffmpeg
  URL http://sourceforge.net/projects/opencvlibrary/files/opencv-unix/2.4.6/opencv-2.4.6.tar.gz
  CMAKE_ARGS -D CMAKE_INSTALL_PREFIX=${WS_PREFIX}
             -D CMAKE_SKIP_RPATH=ON
             -D CMAKE_POSITION_INDEPENDENT_CODE=ON
	     -D CMAKE_EXE_LINKER_FLAGS=-Wl,--as-needed
             -D BUILD_SHARED_LIBS=OFF
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
  BUILD_COMMAND make -j${PROCESSOR_COUNT})

externalproject_add(wushuu_lib
  DEPENDS pkg-config ffmpeg opencv
  URL src/cpp
  CMAKE_ARGS -D CMAKE_INSTALL_PREFIX=${WS_PREFIX}
  CMAKE_ARGS -D WS_PREFIX=${WS_PREFIX})

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
