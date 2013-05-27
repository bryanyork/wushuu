#!/bin/bash -v

FR_PREFIX=$(pwd)/../3rdparty
FFMPEG_VERSION=1.2.1

mkdir -p src && cd src

if [ ! -d ffmpeg-${FFMPEG_VERSION} ]
then
  if [ ! -e ffmpeg-${FFMPEG_VERSION}.tar.bz2 ]
  then
    wget http://www.ffmpeg.org/releases/ffmpeg-${FFMPEG_VERSION}.tar.bz2
  fi
  tar xf ffmpeg-${FFMPEG_VERSION}.tar.bz2
  if [ ! -e ${FR_PREFIX}/bin/sdl-config ]
  then
    ln -s /usr/bin/false ${FR_PREFIX}/bin/sdl-config
  fi
fi

rm -rf ../build/ffmpeg && mkdir -p ../build/ffmpeg && cd ../build/ffmpeg

if [ -n "${WS_BUILD_SHARED}" ]
then
  env -i PATH=${FR_PREFIX}/bin:/usr/bin:/bin \
            ../../src/ffmpeg-${FFMPEG_VERSION}/configure \
                --prefix=${FR_PREFIX} \
                --enable-gpl \
                --enable-nonfree \
                --enable-version3 \
                --disable-encoders \
                --disable-outdevs \
                --disable-doc \
                --disable-programs \
                --disable-static \
                --enable-shared \
                --enable-pic
else
  env -i PATH=${FR_PREFIX}/bin:/usr/bin:/bin \
            ../../src/ffmpeg-${FFMPEG_VERSION}/configure \
                --prefix=${FR_PREFIX} \
                --enable-gpl \
                --enable-nonfree \
                --enable-version3 \
                --disable-encoders \
                --disable-outdevs \
                --disable-doc \
                --disable-programs \
                --enable-static \
                --disable-shared \
                --disable-pic
fi

LD_RUN_PATH=\$ORIGIN make -j8 && make install

cd ../..
