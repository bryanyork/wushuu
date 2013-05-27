#!/bin/bash -v

FR_PREFIX=$(pwd)/../3rdparty
YASM_VERSION=1.2.0

mkdir -p src && cd src

if [ ! -d yasm-${YASM_VERSION} ]
then
  if [ ! -e yasm-${YASM_VERSION}.tar.gz ]
  then
    wget http://www.tortall.net/projects/yasm/releases/yasm-1.2.0.tar.gz
  fi
  tar xf yasm-${YASM_VERSION}.tar.gz
fi

rm -rf ../build/yasm && mkdir -p ../build/yasm && cd ../build/yasm

env -i PATH=${FR_PREFIX}/bin:/usr/bin:/bin \
            ../../src/yasm-${YASM_VERSION}/configure \
                --prefix=${FR_PREFIX} \
                --disable-python \
                --disable-python-binding \
                --disable-nls

make && make install

cd ../..
