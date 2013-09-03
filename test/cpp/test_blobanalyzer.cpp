#include <vector>

#include <stdio.h>

#include "opencv2/highgui/highgui_c.h"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/legacy/legacy.hpp"

typedef void (*bgfg_cb_t)(int x, int y, int w, int h);
bool ProcessFrame(IplImage * detection, IplImage * image, float m_GSD = 20, bgfg_cb_t bgfg_cb = 0);

std::vector<CvRect>  blobs(32);

void blob_collector(int x, int y, int w, int h) {
    blobs.push_back(cvRect(x, y, w, h));
}

int main(int argc, char* argv[]) {
    long frameToLearn = 16;
    cvNamedWindow("result", 1);
#if 1
    CvBGCodeBookModel* model = cvCreateBGCodeBookModel();

    //Set color thresholds to default values
    model->modMin[0] = 3;
    model->modMin[1] = model->modMin[2] = 3;
    model->modMax[0] = 10;
    model->modMax[1] = model->modMax[2] = 10;
    model->cbBounds[0] = model->cbBounds[1] = model->cbBounds[2] = 10;

    CvCapture* capture = cvCreateFileCapture(argv[1]);
    if(3 == argc) {
        frameToLearn = atol(argv[2]);
    }
    IplImage *rawImage = 0, *yuvImage = 0; //yuvImage is for codebook method
    IplImage *imaskCodeBook = 0;
    long frameSeq = 0;

    while(true) {
      rawImage = cvQueryFrame(capture);
      if(!rawImage)
        break;

      ++frameSeq;
      std::cout << frameSeq << std::endl;

      char fnStr[32];
      snprintf(fnStr, sizeof(fnStr), "images/%08ld.jpg", frameSeq);
      cv::Mat mImg = rawImage;

      char fmStr[64];
      blobs.clear();

      if(1 == frameSeq) {
        yuvImage = cvCloneImage(rawImage);
        imaskCodeBook = cvCreateImage( cvGetSize(rawImage), IPL_DEPTH_8U, 1 );
        cvSet(imaskCodeBook,cvScalar(255));
      }

      cvCvtColor( rawImage, yuvImage, CV_BGR2YCrCb );//YUV For codebook method
      if(frameSeq < frameToLearn) {  // learning
        cvBGCodeBookUpdate(model, yuvImage);

        snprintf(fmStr, sizeof(fmStr), "%ld : %d", frameSeq, blobs.size());
        cv::putText(mImg, fmStr, cv::Point(10, 40), cv::FONT_HERSHEY_SIMPLEX, 1, CV_RGB(255, 0, 0));

        cv::Mat mImg = rawImage;
        cv::imwrite(fnStr, mImg);
      } else {
        if(frameSeq == frameToLearn)
          cvBGCodeBookClearStale( model, model->t/2 );

        cvBGCodeBookDiff( model, yuvImage, imaskCodeBook );

        ProcessFrame(imaskCodeBook, rawImage, 20, blob_collector);
        for(std::vector<CvRect>::const_iterator cr = blobs.begin(); cr != blobs.end(); ++cr) {
            std::cout << cr->x << ", " << cr->y << ", " << cr->width << ", " << cr->height << std::endl;
            cv::rectangle(mImg, *cr, CV_RGB(0,255,255));
        }

        snprintf(fmStr, sizeof(fmStr), "%ld : %d", frameSeq, blobs.size());
        cv::putText(mImg, fmStr, cv::Point(10, 40), cv::FONT_HERSHEY_SIMPLEX, 1, CV_RGB(255, 0, 0));

        //cv::imshow("result", mImg);
        //cv::waitKey(0);
        cv::imwrite(fnStr, mImg);
      }
    }
    std::cout << "done" << std::endl;

    cvReleaseImage(&imaskCodeBook);
    cvReleaseImage(&rawImage);
    cvReleaseImage(&yuvImage);
    cvReleaseCapture(&capture);
#else
    cv::Mat m;
    m = cv::imread(argv[1]);
    cv::imshow("result", m);
    cv::waitKey(0);
#endif
}
