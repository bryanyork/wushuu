add_library(wsfd SHARED facedetect.cpp)
target_link_libraries(wsfd ${OpenCV_LIBS})
set_target_properties(wsfd PROPERTIES
  VERSION 1.0
  SOVERSION 1
  INSTALL_RPATH "\$ORIGIN")
install(TARGETS wsfd LIBRARY DESTINATION lib)

add_executable(fd facedetect.cpp)
target_link_libraries(fd ${OpenCV_LIBS})
set_target_properties(fd PROPERTIES
  INSTALL_RPATH "${CMAKE_INSTALL_PREFIX}/lib")
install(TARGETS fd RUNTIME DESTINATION bin)
