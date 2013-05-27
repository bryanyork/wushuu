#!/bin/bash -v

OLD_DIR=$(pwd)
FR_PREFIX=${OLD_DIR}/../3rdparty
YASM_VERSION=1.2.0

SRC_DIR=${OLD_DIR}/src
BUILD_DIR=${OLD_DIR}/build

mkdir -p ${SRC_DIR} && cd ${SRC_DIR}

if [ ! -d yasm-${YASM_VERSION} ]
then
  if [ ! -e yasm-${YASM_VERSION}.tar.gz ]
  then
    wget http://www.tortall.net/projects/yasm/releases/yasm-1.2.0.tar.gz
  fi
  tar xf yasm-${YASM_VERSION}.tar.gz
fi

rm -rf ../build/yasm && mkdir -p build/yasm && cd build/yasm

env -i PATH=${FR_PREFIX}/bin:/usr/bin:/bin \
            ${OLD_DIR}/yasm-${YASM_VERSION}/configure \
                --prefix=${FR_PREFIX} \
                --disable-python \
                --disable-python-binding \
                --disable-nls

make && make install

cd ${OLD_DIR}
