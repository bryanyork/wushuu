#include "facedetect.hpp"

#include <iostream>
#include <vector>

#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

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


void detectAndDraw( cv::Mat& img,
                   cv::CascadeClassifier& cascade,
                   double scale);


fd_cb_t g_fd_cb = 0;

extern "C" void set_cb(fd_cb_t fc) {
    g_fd_cb = fc;
}


int facedetect( double scale, const char* cascadeXml, const char* videoFile )
{
    CvCapture* capture = 0;
    cv::Mat frame, frameCopy, image;
    
    cv::CascadeClassifier cascade;
    
    if( !cascade.load( cascadeXml ) )
    {
        std::cerr << "ERROR: Could not load classifier cascade" << std::endl;
        return -1;
    }
    
    capture = cvCaptureFromAVI( videoFile );
    if(!capture)
    {
        std::cerr << "Capture from AVI didn't work" << std::endl;
        return 0;
    }
    
    if( capture )
    {
        int x = 0;
        for(;;)
        {
            IplImage* iplImg = cvQueryFrame( capture );
            frame = iplImg;
            if( frame.empty() )
                break;
            if( iplImg->origin == IPL_ORIGIN_TL )
                frame.copyTo( frameCopy );
            else
                flip( frame, frameCopy, 0 );

            detectAndDraw( frameCopy, cascade, scale );

        }

        cvReleaseCapture( &capture );
    }

    return 0;
}

void detectAndDraw( cv::Mat& img,
                   cv::CascadeClassifier& cascade,
                   double scale)
{
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
    cv::Mat gray, smallImg( cvRound (img.rows/scale), cvRound(img.cols/scale), CV_8UC1 );
    
    cv::cvtColor( img, gray, CV_BGR2GRAY );
    cv::resize( gray, smallImg, smallImg.size(), 0, 0, cv::INTER_LINEAR );
    equalizeHist( smallImg, smallImg );
    
    t = (double)cvGetTickCount();
    cascade.detectMultiScale( smallImg, faces,
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
        center.x = cvRound((r->x + r->width*0.5)*scale);
        center.y = cvRound((r->y + r->height*0.5)*scale);
        radius = cvRound((r->width + r->height)*0.25*scale);
        
        std::cout << center.x << " " << center.y << " " << radius << std::endl;
        if(g_fd_cb) {
            g_fd_cb(center.x, center.y, radius);
        }
    }
}

extern "C" void test1()
{
    std::cout << "from test" << std::endl;
}

int main( int argc, const char** argv )
{
    std::cout << "calling with " << argc << ", " << argv[0] << ", " << argv[1] << std::endl;
    facedetect(1.0, argv[1], argv[2]);
}

extern "C" void test2(int argc, const char** argv)
{
    std::cout << "***" << argv[0] << std::endl;
    std::cout << "***" << argv[1] << std::endl;
    facedetect(1.0, argv[0], argv[1]);
}

