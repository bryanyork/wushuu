#!/bin/bash -v

rm -rf $(pwd)/../3rdparty && rm -rf build

. build_pkg-config.sh
. build_yasm.sh
. build_ffmpeg.sh
. build_opencv.sh
