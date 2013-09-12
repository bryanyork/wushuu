cmake_minimum_required(VERSION 2.8)

project(wushuu-native)

include(${WS_PREFIX}/../cmake/FindDependencies.cmake)
include(${WS_PREFIX}/../cmake/CompilerFlags.cmake)

add_library(wsnative SHARED facedetect.cpp bgfg_codebook.cpp blobanalyzer.cpp)
target_link_libraries(wsnative ${OpenCV_LIBS} ${FFmpeg_LIBRARIES} ${X264_LIBRARIES} ${Boost_LIBRARIES})

include(GenerateExportHeader)
GENERATE_EXPORT_HEADER(wsnative)
ADD_COMPILER_EXPORT_FLAGS()

set_target_properties(wsnative PROPERTIES
  VERSION 1.0
  SOVERSION 1
  COMPILE_FLAGS "-I${CMAKE_CURRENT_BINARY_DIR}"  # for export header
  LINK_FLAGS "-Wl,--as-needed -Wl,-Bsymbolic"
  INSTALL_RPATH "\$ORIGIN")

install(TARGETS wsnative EXPORT wsnative
  ARCHIVE DESTINATION lib
  LIBRARY DESTINATION lib
  RUNTIME DESTINATION bin)
install(EXPORT wsnative DESTINATION lib)

