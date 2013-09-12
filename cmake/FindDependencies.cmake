
set(CMAKE_FIND_ROOT_PATH ${WS_PREFIX})

if(UNIX)
  find_package(PkgConfig REQUIRED)
  pkg_check_modules(FFmpeg REQUIRED libavformat libavcodec libavutil libswscale)
  pkg_check_modules(X264 REQUIRED x264)
endif()
find_package(OpenCV REQUIRED ONLY_CMAKE_FIND_ROOT_PATH)

####################################################
#set(Boost_USE_STATIC_LIBS ON)
#set(Boost_USE_STATIC_RUNTIME ON)
#find_package(Boost 1.54 EXACT REQUIRED
#  COMPONENTS thread system)
####################################################

