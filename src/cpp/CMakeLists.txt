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

add_custom_command(TARGET wsfd POST_BUILD
  COMMAND cmake -E cmake_echo_color --red --bold "Creating wsnatives.jar ..."
  COMMAND ldd $<TARGET_LINKER_FILE:wsfd> | fgrep ${WS_PREFIX} | cut -d " " -f 3 | sed -e "s#.*/##" > ${WS_PREFIX}/wsnatives.list
  COMMAND jar ARGS cfM ${CMAKE_BINARY_DIR}/res/native/wsnatives.jar @${WS_PREFIX}/wsnatives.list
  COMMAND jar ARGS ufM ${CMAKE_BINARY_DIR}/res/native/wsnatives.jar $<TARGET_LINKER_FILE_NAME:wsfd>
  WORKING_DIRECTORY ${WS_PREFIX}/lib)
