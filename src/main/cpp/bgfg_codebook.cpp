#include "bgfg_codebook.hpp"

#include "opencv2/highgui/highgui_c.h"
#include "opencv2/legacy/legacy.hpp"

#include "stdio.h"

bool ProcessFrame(IplImage * detection, IplImage * image, float m_GSD = 20, bgfg_cb_t bgfg_cb = 0);

wushuu::BgFgCodeBook* bgfgcb_create(bgfg_cb_t bgfg_cb) {
  return new wushuu::BgFgCodeBook(bgfg_cb);
}

void bgfgcb_destroy(wushuu::BgFgCodeBook* bf) {
  delete bf;
}

void bgfgcb_detect_video(wushuu::BgFgCodeBook* bf, const char* videoFile) {
        printf("==============================\n");
        printf("=========   ENTER  ===========\n");
        printf("==============================\n");
  bf->detectVideo(videoFile);
}

namespace wushuu {

  BgFgCodeBook::BgFgCodeBook(bgfg_cb_t bgfg_cb):bgfg_cb_(bgfg_cb) {
  }

  void BgFgCodeBook::detectVideo(const char* video, const unsigned int frameToLearn) {
    CvBGCodeBookModel* model = cvCreateBGCodeBookModel();

    //Set color thresholds to default values
    model->modMin[0] = 3;
    model->modMin[1] = model->modMin[2] = 3;
    model->modMax[0] = 10;
    model->modMax[1] = model->modMax[2] = 10;
    model->cbBounds[0] = model->cbBounds[1] = model->cbBounds[2] = 10;

    CvCapture* capture = cvCreateFileCapture(video);
    IplImage *rawImage = 0, *yuvImage = 0; //yuvImage is for codebook method
    IplImage *imaskCodeBook = 0;
    unsigned int frameSeq = 0;

    while(true) {
      rawImage = cvQueryFrame(capture);
      if(!rawImage)
      {
        printf("==============================\n");
        printf("=========NO   FRAME===========\n");
        printf("==============================\n");
        break;
      }

      ++frameSeq;
        printf("==============================\n");
        printf("=========ANOTHERAME===========\n");
        printf("==============================\n");

      if(1 == frameSeq) {
        yuvImage = cvCloneImage(rawImage);
        imaskCodeBook = cvCreateImage( cvGetSize(rawImage), IPL_DEPTH_8U, 1 );
        cvSet(imaskCodeBook,cvScalar(255));
      }

      cvCvtColor( rawImage, yuvImage, CV_BGR2YCrCb );//YUV For codebook method
      if(frameSeq < frameToLearn) {  // learning
        cvBGCodeBookUpdate(model, yuvImage);
      } else {
        if(frameSeq == frameToLearn)
          cvBGCodeBookClearStale( model, model->t/2 );

        cvBGCodeBookDiff( model, yuvImage, imaskCodeBook );
        ProcessFrame(imaskCodeBook, rawImage, 20, bgfg_cb_);
      }
    }

    cvReleaseImage(&imaskCodeBook);
    cvReleaseImage(&rawImage);
    cvReleaseImage(&yuvImage);
    cvReleaseCapture(&capture);
  }

  BgFgCodeBook::~BgFgCodeBook() {
  }
}

