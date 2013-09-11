#ifndef WUSHUU_FACEDETECT_HPP
#define WUSHUU_FACEDETECT_HPP

#include "wsnative_export.h"

#include "opencv2/objdetect/objdetect.hpp"

typedef void (*fd_cb_t)(int x, int y, int radius);

namespace cv {
  class CascadeClassifier;
}

namespace wushuu {
  class FaceDetect {
    public:
      FaceDetect(const char* cascadeXml, fd_cb_t fd_cb, double scale = 1.0);
      void detectImage(const char* imgFile, fd_cb_t fd_cb = 0);
      void detectVideo(const char* videoFile);

    private:
      void detect(cv::Mat& img, fd_cb_t fd_cb = 0);

    private:
      double scale_;
      fd_cb_t fd_cb_;
      cv::CascadeClassifier cascade_;
  };
}

extern "C" {

WSNATIVE_EXPORT wushuu::FaceDetect* facedetect_create(const char* cascadeXml, fd_cb_t fd_cb);
WSNATIVE_EXPORT void facedetect_destroy(wushuu::FaceDetect* fd);
WSNATIVE_EXPORT void facedetect_detect_image(wushuu::FaceDetect* fd, const char* imgFile, fd_cb_t fd_cb);
WSNATIVE_EXPORT void facedetect_detect_video(wushuu::FaceDetect* fd, const char* videoFile);

}

#endif
