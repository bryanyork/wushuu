include_directories(BEFORE ${WS_PREFIX}/include) 
link_directories(${WS_PREFIX}/lib)

set(OPENCV_LIBS opencv_core opencv_imgproc opencv_objdetect opencv_highgui)

set(CMAKE_BUILD_WITH_INSTALL_RPATH TRUE)

add_library(wsfd SHARED facedetect.cpp)
target_link_libraries(wsfd ${OPENCV_LIBS})
set_target_properties(wsfd PROPERTIES
  VERSION 1.0
  SOVERSION 1
  LIBRARY_OUTPUT_DIRECTORY ${WS_PREFIX}/lib
  INSTALL_RPATH "\$ORIGIN")

add_executable(fd facedetect.cpp)
target_link_libraries(fd ${OPENCV_LIBS})
set_target_properties(fd PROPERTIES
  RUNTIME_OUTPUT_DIRECTORY ${WS_PREFIX}/bin
  INSTALL_RPATH "\$ORIGIN/../lib")
