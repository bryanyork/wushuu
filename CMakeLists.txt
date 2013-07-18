cmake_minimum_required(VERSION 2.8)

project(wushuu)

include(ProcessorCount)
ProcessorCount(PROCESSOR_COUNT)

set(WS_PREFIX ${CMAKE_BINARY_DIR}/target)

include(ExternalProject)
set_directory_properties(PROPERTIES EP_PREFIX ${WS_PREFIX})

function(ws_ep_src_url NAME LOCAL REMOTE)
  if(EXISTS ${CMAKE_SOURCE_DIR}/3rdparty/${LOCAL})
    set(WS_${NAME}_URL ${CMAKE_SOURCE_DIR}/3rdparty/${LOCAL} PARENT_SCOPE)
  else()
    set(WS_${NAME}_URL ${REMOTE} PARENT_SCOPE)
  endif()
endfunction()


if(UNIX)
  set(CMAKE_BUILD_TOOL "${CMAKE_BUILD_TOOL} -j ${PROCESSOR_COUNT}")

  ws_ep_src_url(PKG_CONFIG pkg-config-0.28.tar.gz http://pkgconfig.freedesktop.org/releases/pkg-config-0.28.tar.gz)
  externalproject_add(pkg-config
    URL ${WS_PKG_CONFIG_URL}
    CONFIGURE_COMMAND <SOURCE_DIR>/configure --prefix=${WS_PREFIX}
                                             --with-internal-glib)

  ws_ep_src_url(X264 last_x264.tar.gz ftp://ftp.videolan.org/pub/x264/snapshots/last_x264.tar.bz2)
  externalproject_add(x264
    URL ${WS_X264_URL}
    CONFIGURE_COMMAND <SOURCE_DIR>/configure --prefix=${WS_PREFIX}
                                             --enable-pic
                                             --enable-static
                                             --extra-cflags=-fvisibility=hidden
                                             --disable-opencl)

  ws_ep_src_url(FFMPEG ffmpeg-1.2.1.tar.bz2 http://www.ffmpeg.org/releases/ffmpeg-1.2.1.tar.bz2)
  externalproject_add(ffmpeg
    DEPENDS x264
    URL ${WS_FFMPEG_URL}
    CONFIGURE_COMMAND <SOURCE_DIR>/configure --prefix=${WS_PREFIX}
                                             --enable-gpl
                                             --enable-nonfree
                                             --enable-version3
                                             --enable-libx264
                                             --disable-devices
                                             --disable-doc
                                             --disable-shared
                                             --enable-pic
                                             --enable-static
                                             --enable-debug
                                             --disable-stripping
                                             --extra-cflags=-I${WS_PREFIX}/include
                                             --extra-cflags=-fvisibility=hidden
                                             --extra-ldflags=-L${WS_PREFIX}/lib)
endif()

ws_ep_src_url(OPENCV opencv-2.4.6.tar.gz http://sourceforge.net/projects/opencvlibrary/files/opencv-unix/2.4.6/opencv-2.4.6.tar.gz)
externalproject_add(opencv
  DEPENDS pkg-config ffmpeg
  URL ${WS_OPENCV_URL}
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
             -D WITH_JASPER=ON
             -D BUILD_JASPER=ON
             -D WITH_JPEG=ON
             -D BUILD_JPEG=ON
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
             -D BUILD_opencv_world=OFF)

ws_ep_src_url(BOOST boost_1_54_0.tar.bz2 http://sourceforge.net/projects/boost/files/boost/1.54.0/boost_1_54_0.tar.bz2)
externalproject_add(boost
  URL ${WS_BOOST_URL}
  CONFIGURE_COMMAND ./bootstrap.sh --prefix=${WS_PREFIX}
                                   --with-libraries=thread
  BUILD_COMMAND ./b2 -j ${PROCESSOR_COUNT} link=static cxxflags=-fPIC
  INSTALL_COMMAND ./b2 install link=static
  BUILD_IN_SOURCE 1)

externalproject_add(wushuu-native
  DEPENDS pkg-config ffmpeg opencv boost
  URL src/cpp
  CMAKE_ARGS -D CMAKE_INSTALL_PREFIX=${WS_PREFIX}
             -D WS_PREFIX=${WS_PREFIX})

externalproject_add_step(wushuu-native force-rebuild
  COMMAND cmake -E cmake_echo_color --red --bold "Forcing rebuild of wushuu-native ..."
  DEPENDERS download
  ALWAYS 1)

if(CMAKE_SIZEOF_VOID_P EQUAL 8)
  set(JNA_ARCH x86-64)
else()
  set(JNA_ARCH x86)
endif()
string(TOLOWER ${CMAKE_SYSTEM_NAME} JNA_OS)

externalproject_add_step(wushuu-native stage
  COMMAND cmake -E cmake_echo_color --red --bold "Staging native libraries ..."
  COMMAND cmake -E copy_if_different <INSTALL_DIR>/lib/libwsnative.so res/${JNA_OS}-${JNA_ARCH}/libwsnative.so
  DEPENDEES install)

add_custom_target(jvm
  COMMAND cmake -E cmake_echo_color --red --bold "Compiling Java sources ..."
  COMMAND lein javac
  COMMAND cmake -E cmake_echo_color --red --bold "Compiling Clojure sources ..."
  COMMAND lein compile
  DEPENDS wushuu-native)

add_custom_target(storm
  COMMAND cmake -E cmake_echo_color --red --bold "Making storm topology ..."
  COMMAND lein uberjar
  DEPENDS jvm)
