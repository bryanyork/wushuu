#!/bin/bash -v

OLD_DIR=$(pwd)
FR_PREFIX=${OLD_DIR}/../3rdparty
PC_VERSION=0.28

mkdir -p src && cd src

if [ ! -d pkg-config-${PC_VERSION} ]
then
  if [ ! -e pkg-config-${PC_VERSION}.tar.gz ]
  then
    wget http://pkgconfig.freedesktop.org/releases/pkg-config-${PC_VERSION}.tar.gz
  fi
  tar xf pkg-config-${PC_VERSION}.tar.gz
fi

rm -rf ../build/pkg-config && mkdir -p ../build/pkg-config && cd ../build/pkg-config

env -i PATH=${FR_PREFIX}/bin:/usr/bin:/bin \
            ${OLD_DIR}/pkg-config-${PC_VERSION}/configure \
                --prefix=${FR_PREFIX} \
                --with-internal-glib

make && make install

cd ${OLD_DIR}
