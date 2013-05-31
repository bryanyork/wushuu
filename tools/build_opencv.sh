#!/bin/bash -v

FR_PREFIX=$(pwd)/../3rdparty
OPENCV_VERSION=2.4.5

mkdir -p src && cd src

if [ ! -d opencv-${OPENCV_VERSION} ]
then
  if [ ! -e opencv-${OPENCV_VERSION}.tar.gz ]
  then
    wget http://sourceforge.net/projects/opencvlibrary/files/opencv-unix/${OPENCV_VERSION}/opencv-${OPENCV_VERSION}.tar.gz/download
  fi
  tar xf opencv-${OPENCV_VERSION}.tar.gz
fi

rm -rf ../build/opencv && mkdir -p ../build/opencv && cd ../build/opencv

if [ -n "${WS_BUILD_SHARED}" ]
then
  env -i PATH=${FR_PREFIX}/bin:/usr/local/bin:/usr/bin:/bin \
            cmake -D BUILD_SHARED_LIBS=ON \
                  -D CMAKE_C_COMPILER=gcc44 \
                  -D CMAKE_C_FLAGS="${WS_COMPILE_FLAGS}" \
                  -D CMAKE_CXX_COMPILER=g++44 \
                  -D CMAKE_CXX_FLAGS="${WS_COMPILE_FLAGS}" \
                  -D CMAKE_INSTALL_PREFIX=${FR_PREFIX} \
                  -D CMAKE_SHARED_LINKER_FLAGS="-L${FR_PREFIX}/lib ${WS_LINK_FLAGS}" \
                  -D CMAKE_SKIP_RPATH=ON \
                  -D BUILD_JPEG=ON \
                  -D BUILD_PNG=ON \
                  -D BUILD_TIFF=ON \
                  -D BUILD_ZLIB=ON \
                  -D BUILD_JASPER=ON \
                  -D BUILD_OPENEXR=ON \
                  -D WITH_1394=OFF \
                  -D BUILD_DOCS=OFF \
                  -D BUILD_PERF_TESTS=OFF \
                  -D BUILD_TESTS=OFF \
                  -D BUILD_opencv_python=OFF \
                  -D BUILD_opencv_java=OFF \
                  -D BUILD_opencv_world=ON \
                  -D BUILD_EXAMPLES=OFF \
                  -D BUILD_opencv_apps=OFF \
                  ../../src/opencv-${OPENCV_VERSION}
else
  env -i PATH=${FR_PREFIX}/bin:/usr/local/bin:/usr/bin:/bin \
            CC=/usr/bin/gcc44 \
            CXX=/usr/bin/g++44 \
            cmake -D BUILD_SHARED_LIBS=OFF \
                  -D COMPILE_FLAGS="${WS_COMPILE_FLAGS}" \
                  -D LINK_FLAGS="${WS_LINK_FLAGS}" \
                  -D CMAKE_C_COMPILER=gcc44 \
                  -D CMAKE_C_FLAGS="${WS_COMPILE_FLAGS}" \
                  -D CMAKE_CXX_COMPILER=g++44 \
                  -D CMAKE_CXX_FLAGS="${WS_COMPILE_FLAGS}" \
                  -D CMAKE_INSTALL_PREFIX=${FR_PREFIX} \
                  -D CMAKE_SHARED_LINKER_FLAGS="-L${FR_PREFIX}/lib ${WS_LINK_FLAGS}" \
                  -D BUILD_JPEG=ON \
                  -D BUILD_PNG=ON \
                  -D BUILD_TIFF=ON \
                  -D BUILD_ZLIB=ON \
                  -D BUILD_JASPER=ON \
                  -D BUILD_OPENEXR=ON \
                  -D WITH_1394=OFF \
                  -D BUILD_DOCS=OFF \
                  -D BUILD_PERF_TESTS=OFF \
                  -D BUILD_TESTS=OFF \
                  -D BUILD_opencv_python=OFF \
                  -D BUILD_opencv_java=OFF \
                  -D BUILD_opencv_world=ON \
                  -D BUILD_EXAMPLES=OFF \
                  -D BUILD_opencv_apps=OFF \
                  ../../src/opencv-${OPENCV_VERSION}
fi

LD_RUN_PATH=\$ORIGIN make -j8 && make install

cd ../..
