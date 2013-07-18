#include "facedetect.hpp"

#include <iostream>
#include <vector>

#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

wushuu::FaceDetect* facedetect_create(const char* cascadeXml, fd_cb_t fd_cb) {
  return new wushuu::FaceDetect(cascadeXml, fd_cb);
}

void facedetect_destroy(wushuu::FaceDetect* fd) {
  delete fd;
}

void facedetect_detect_image(wushuu::FaceDetect* fd, const char* imgFile) {
  fd->detectImage(imgFile);
}

namespace wushuu {

FaceDetect::FaceDetect(const char* cascadeXml, fd_cb_t fd_cb, double scale):fd_cb_(fd_cb), scale_(scale) {
  cascade_.load(cascadeXml);
}

void FaceDetect::detectImage(const char* imgFile) {
  cv::Mat img = cv::imread(imgFile);
  std::cout << imgFile << std::endl;
  std::cout << img.channels() << std::endl;
  detect(img);
}

void FaceDetect::detect(cv::Mat& img) {
  int i = 0;
  double t = 0;
  std::vector<cv::Rect> faces;
  const static cv::Scalar colors[] =  { CV_RGB(0,0,255),
      CV_RGB(0,128,255),
      CV_RGB(0,255,255),
      CV_RGB(0,255,0),
      CV_RGB(255,128,0),
      CV_RGB(255,255,0),
      CV_RGB(255,0,0),
      CV_RGB(255,0,255)} ;
  cv::Mat gray, smallImg( cvRound (img.rows/scale_), cvRound(img.cols/scale_), CV_8UC1 );

  cv::cvtColor( img, gray, CV_BGR2GRAY );
  cv::resize( gray, smallImg, smallImg.size(), 0, 0, cv::INTER_LINEAR );
  equalizeHist( smallImg, smallImg );

  t = (double)cvGetTickCount();
  cascade_.detectMultiScale( smallImg, faces,
          1.1, 2, 0
          //|CV_HAAR_FIND_BIGGEST_OBJECT
          //|CV_HAAR_DO_ROUGH_SEARCH
          |CV_HAAR_SCALE_IMAGE
          ,
          cv::Size(30, 30) );
  t = (double)cvGetTickCount() - t;

  for( std::vector<cv::Rect>::const_iterator r = faces.begin(); r != faces.end(); r++, i++ )
  {
    cv::Mat smallImgROI;
    cv::Point center;
    cv::Scalar color = colors[i%8];
    int radius;
    center.x = cvRound((r->x + r->width*0.5)*scale_);
    center.y = cvRound((r->y + r->height*0.5)*scale_);
    radius = cvRound((r->width + r->height)*0.25*scale_);

    std::cout << center.x << " " << center.y << " " << radius << std::endl;
    if(fd_cb_) {
      fd_cb_(center.x, center.y, radius);
    }
  }
}

}


int main( int argc, const char** argv )
{
  return 0;
}


