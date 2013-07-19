#ifndef WUSHUU_FACEDETECT_HPP
#define WUSHUU_FACEDETECT_HPP

#include "opencv2/objdetect/objdetect.hpp"

typedef void (*fd_cb_t)(int x, int y, int radius);

namespace wushuu {
  class FaceDetect {
    public:
      FaceDetect(const char* cascadeXml, fd_cb_t fd_cb, double scale = 1.0);
      void detectImage(const char* imgFile);
      void detectVideo(const char* videoFile);

    private:
      void detect(cv::Mat& img);

    private:
      double scale_;
      fd_cb_t fd_cb_;
      cv::CascadeClassifier cascade_;
  };
}

extern "C" {

wushuu::FaceDetect* facedetect_create(const char* cascadeXml, fd_cb_t fd_cb);
void facedetect_destroy(wushuu::FaceDetect* fd);
void facedetect_detect_image(wushuu::FaceDetect* fd, const char* imgFile);
void facedetect_detect_video(wushuu::FaceDetect* fd, const char* videoFile);

}

#endif
