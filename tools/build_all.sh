#!/bin/bash -v

rm -rf $(pwd)/../3rdparty && rm -rf build

# have to integrate device support
export WS_BUILD_SHARED=true
# hikvision only have 32bit decode SDK for linux
export WS_COMPILE_FLAGS=-m32
export WS_COMPILE_FLAGS=-m32

. build_pkg-config.sh
. build_yasm.sh
. build_ffmpeg.sh
. build_opencv.sh
