#!/bin/bash -v

WDIR=$(pwd)
FR_PREFIX=${WDIR}/../3rdparty
FFMPEG_VERSION=1.2.1

if [ ! -d ffmpeg-${FFMPEG_VERSION} ]
then
  if [ ! -e ffmpeg-${FFMPEG_VERSION}.tar.bz2 ]
  then
    wget http://www.ffmpeg.org/releases/ffmpeg-${FFMPEG_VERSION}.tar.bz2
  fi
  tar xf ffmpeg-${FFMPEG_VERSION}.tar.bz2
  patch -N -d ffmpeg-${FFMPEG_VERSION} < ffmpeg-${FFMPEG_VERSION}.patch
fi

rm -rf build/ffmpeg && mkdir -p build/ffmpeg && cd build/ffmpeg

${WDIR}/ffmpeg-${FFMPEG_VERSION}/configure --prefix=${FR_PREFIX} --enable-gpl --enable-nonfree --enable-version3 --disable-encoders --disable-outdevs --disable-doc --disable-static --disable-programs --enable-shared --enable-pic
make -j8 && make install

cd ${WDIR}
