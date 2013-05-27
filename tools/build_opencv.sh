#!/bin/bash -v

OLD_DIR=$(pwd)
FR_PREFIX=${OLD_DIR}/../3rdparty
OPENCV_VERSION=2.4.5

if [ ! -d opencv-${OPENCV_VERSION} ]
then
  if [ ! -e opencv-${OPENCV_VERSION}.tar.gz ]
  then
    wget http://sourceforge.net/projects/opencvlibrary/files/opencv-unix/${OPENCV_VERSION}/opencv-${OPENCV_VERSION}.tar.gz/download
  fi
  tar xf opencv-${OPENCV_VERSION}.tar.gz
fi

rm -rf build/opencv && mkdir -p build/opencv && cd build/opencv

if [ -n "${WS_BUILD_SHARED}" ]
then
  env -i PATH=${FR_PREFIX}/bin:/usr/local/bin:/usr/bin:/bin \
            CC=/usr/bin/gcc44 \
            CXX=/usr/bin/g++44 \
            cmake -D BUILD_SHARED_LIBS=ON \
                  -D CMAKE_INSTALL_PREFIX=${FR_PREFIX} \
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
                  ${OLD_DIR}/opencv-${OPENCV_VERSION}
else
  env -i PATH=${FR_PREFIX}/bin:/usr/local/bin:/usr/bin:/bin \
            CC=/usr/bin/gcc44 \
            CXX=/usr/bin/g++44 \
            cmake -D BUILD_SHARED_LIBS=OFF \
                  -D CMAKE_INSTALL_PREFIX=${FR_PREFIX} \
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
                  ${OLD_DIR}/opencv-${OPENCV_VERSION}
fi

LD_RUN_PATH=\$ORIGIN make -j8 && make install

cd ${OLD_DIR}
