cmake_minimum_required(VERSION 2.8)

project(wushuu-native-test)

include(${WS_PREFIX}/../cmake/FindDependencies.cmake)
include(${WS_PREFIX}/../cmake/CompilerFlags.cmake)

include(${WS_PREFIX}/lib/wsnative.cmake)

add_library(wsnativetest SHARED test.cpp)
target_link_libraries(wsnativetest ${OpenCV_LIBS} ${FFmpeg_LIBRARIES} ${X264_LIBRARIES})
set_target_properties(wsnativetest PROPERTIES
  VERSION 1.0
  SOVERSION 1
  LINK_FLAGS "-Wl,--as-needed -Wl,-Bsymbolic"
  INSTALL_RPATH "\$ORIGIN")

add_executable(wsnt test.cpp)
target_link_libraries(wsnt ${OpenCV_LIBS} ${FFmpeg_LIBRARIES} ${X264_LIBRARIES})
set_target_properties(wsnt PROPERTIES
  LINK_FLAGS "-Wl,--as-needed"
  INSTALL_RPATH "\$ORIGIN/../lib")

add_executable(test_blobanalyzer test_blobanalyzer.cpp)
target_link_libraries(test_blobanalyzer ${OpenCV_LIBS} ${FFmpeg_LIBRARIES} ${X264_LIBRARIES} wsnative)
set_target_properties(test_blobanalyzer PROPERTIES
  LINK_FLAGS "-Wl,--as-needed"
  INSTALL_RPATH "\$ORIGIN/../lib")

install(TARGETS wsnativetest wsnt test_blobanalyzer
  RUNTIME DESTINATION bin
  LIBRARY DESTINATION lib)
