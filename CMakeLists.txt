cmake_minimum_required(VERSION 2.8)

project(wushuu)

set(WS_PREFIX ${CMAKE_BINARY_DIR}/target)
set(EP_DOWNLOAD_DIR 3rdparty)

include(ExternalProject)
set_directory_properties(PROPERTIES EP_PREFIX ${WS_PREFIX})

if(UNIX)
  externalproject_add(x264
    URL ftp://ftp.videolan.org/pub/x264/snapshots/last_x264.tar.bz2
    URL_MD5 6a36c66bd43e88c5b0aa39405a744801
    DOWNLOAD_DIR ${EP_DOWNLOAD_DIR}
    CONFIGURE_COMMAND <SOURCE_DIR>/configure --prefix=${WS_PREFIX}
                                             --enable-pic
                                             --enable-static
                                             --extra-cflags=-fvisibility=hidden
                                             --disable-opencl)

  externalproject_add(ffmpeg
    DEPENDS x264
    URL http://www.ffmpeg.org/releases/ffmpeg-1.2.1.tar.bz2
    URL_MD5 5071a26cc149f380908ce79ec2a677ef
    DOWNLOAD_DIR ${EP_DOWNLOAD_DIR}
    CONFIGURE_COMMAND <SOURCE_DIR>/configure --prefix=${WS_PREFIX}
                                             --enable-gpl
                                             --enable-nonfree
                                             --enable-version3
                                             --enable-libx264
                                             --disable-devices
                                             --enable-indev=lavfi
                                             --disable-doc
                                             --disable-shared
                                             --enable-pic
                                             --enable-static
                                             --enable-debug
                                             --disable-stripping
                                             --extra-cflags=-I${WS_PREFIX}/include
                                             --extra-cflags=-fvisibility=hidden
                                             --extra-ldflags=-L${WS_PREFIX}/lib)
  set(Optional_Externals x264 ffmpeg)
endif()

# help opencv tofind our ffmpeg
set(ENV{PKG_CONFIG_PATH} ${WS_PREFIX}/lib/pkgconfig)  

externalproject_add(opencv
  DEPENDS ${Optional_Externals}
  URL http://sourceforge.net/projects/opencvlibrary/files/opencv-unix/2.4.6/opencv-2.4.6.tar.gz
  URL_MD5 c5e8758a419b4f258611b17fc83aafaf
  DOWNLOAD_DIR ${EP_DOWNLOAD_DIR}
  CMAKE_ARGS -D CMAKE_INSTALL_PREFIX=${WS_PREFIX}
             -D CMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE}
             -D CMAKE_EXE_LINKER_FLAGS=-L${WS_PREFIX}/lib # our lib path
             -D CMAKE_SKIP_RPATH=ON
             -D CMAKE_POSITION_INDEPENDENT_CODE=ON
             -D BUILD_SHARED_LIBS=OFF
             -D WITH_1394=OFF
             -D WITH_AVFOUNDATION=OFF
             -D WITH_CARBON=OFF
             -D WITH_CUDA=OFF
             -D WITH_CUBLAS=OFF
             -D WITH_NVCUVID=OFF
             -D WITH_EIGEN=OFF
             -D WITH_VFW=OFF
             -D WITH_GSTREAMER=OFF
             -D WITH_GTK=ON
             -D WITH_IMAGEIO=OFF
             -D WITH_IPP=OFF
             ### begin opencv/3rdparty ###
             -D WITH_FFMPEG=ON
             -D WITH_JASPER=ON
             -D BUILD_JASPER=ON
             -D WITH_JPEG=ON
             -D BUILD_JPEG=ON
             -D WITH_PNG=OFF
             -D WITH_TIFF=OFF
             -D WITH_OPENEXR=OFF
             -D WITH_TBB=OFF
             -D BUILD_ZLIB=ON
             ### end opencv/3rdparty ###
             -D WITH_OPENNI=OFF
             -D WITH_PVAPI=OFF
             -D WITH_GIGEAPI=OFF
             -D WITH_QT=OFF
             #-D WITH_WIN32UI  using default
             -D WITH_QUICKTIME=OFF
             -D WITH_CSTRIPES=OFF
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

#if(UNIX)
#  set(BOOST_CFG_CMD ./bootstrap.sh)
#elseif(WIN32)
#  set(BOOST_CFG_CMD ./bootstrap.bat)
#endif()
#externalproject_add(boost
#  URL http://sourceforge.net/projects/boost/files/boost/1.54.0/boost_1_54_0.tar.bz2
#  URL_MD5 15cb8c0803064faef0c4ddf5bc5ca279
#  DOWNLOAD_DIR ${EP_DOWNLOAD_DIR}
#  CONFIGURE_COMMAND ${BOOST_CFG_CMD}
#  BUILD_COMMAND ./b2 link=static runtime-link=static --prefix=${WS_PREFIX} --with-thread
#  INSTALL_COMMAND ./b2 install link=static runtime-link=static --prefix=${WS_PREFIX} --with-thread
#  BUILD_IN_SOURCE 1)

externalproject_add(wushuu-native
  DEPENDS ${Optional_Externals} opencv
  URL src/main/cpp
  CMAKE_ARGS -D CMAKE_INSTALL_PREFIX=${WS_PREFIX}
             -D CMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE}
             -D WS_PREFIX=${WS_PREFIX})

externalproject_add_step(wushuu-native force-rebuild
  COMMAND cmake -E cmake_echo_color --red --bold "Forcing rebuild of wushuu-native ..."
  DEPENDERS download
  ALWAYS 1)

externalproject_add(wushuu-native-test
  DEPENDS ${Optional_Externals} opencv wushuu-native
  URL src/test/cpp
  CMAKE_ARGS -D CMAKE_INSTALL_PREFIX=${WS_PREFIX}
             -D CMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE}
             -D WS_PREFIX=${WS_PREFIX})

externalproject_add_step(wushuu-native-test force-rebuild
  COMMAND cmake -E cmake_echo_color --red --bold "Forcing rebuild of wushuu-native-test ..."
  DEPENDERS download
  ALWAYS 1)

if(CMAKE_SIZEOF_VOID_P EQUAL 8)
  set(JNA_ARCH x86-64)
else()
  set(JNA_ARCH x86)
endif()

if(WIN32)
  set(JNA_OS win32)  # cmake return "Windows" for ${CMAKE_SYSTEM_NAME}
else()
  string(TOLOWER ${CMAKE_SYSTEM_NAME} JNA_OS)
endif()

externalproject_add_step(wushuu-native stage
  COMMAND cmake -E cmake_echo_color --red --bold "Staging native libraries ..."
  COMMAND cmake -E copy_if_different <INSTALL_DIR>/bin/${CMAKE_SHARED_LIBRARY_PREFIX}wsnative${CMAKE_SHARED_LIBRARY_SUFFIX} <INSTALL_DIR>/resource/${JNA_OS}-${JNA_ARCH}/${CMAKE_SHARED_LIBRARY_PREFIX}wsnative${CMAKE_SHARED_LIBRARY_SUFFIX}
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