#!/bin/bash -ev

#rm -rf $(pwd)/../3rdparty && rm -rf build

# have to integrate device support
export WS_BUILD_SHARED=true
export WS_BUILD_32BIT=true

if [ -n ${WS_BUILD_32BIT} ]
then
  # hikvision only have 32bit decode SDK for linux
  export WS_COMPILE_FLAGS=-m32
  export WS_LINK_FLAGS=-m32
  export WS_LIBRARY_PATH=/lib
else
  export WS_LIBRARY_PATH=/lib64
fi

. build_pkg-config.sh && . build_ffmpeg.sh && . build_opencv.sh
