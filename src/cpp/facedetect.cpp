#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#include <iostream>
#include <stdio.h>

using namespace std;
using namespace cv;


void detectAndDraw( Mat& img,
                   CascadeClassifier& cascade,
                   double scale);


int facedetect( double scale, const char* cascadeXml, const char* videoFile )
{
    CvCapture* capture = 0;
    Mat frame, frameCopy, image;
    
    CascadeClassifier cascade;
    
    if( !cascade.load( cascadeXml ) )
    {
        cerr << "ERROR: Could not load classifier cascade" << endl;
        return -1;
    }
    
    capture = cvCaptureFromAVI( videoFile );
    if(!capture)
    {
        cerr << "Capture from AVI didn't work" << endl;
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

void detectAndDraw( Mat& img,
                   CascadeClassifier& cascade,
                   double scale)
{
    int i = 0;
    double t = 0;
    vector<Rect> faces;
    const static Scalar colors[] =  { CV_RGB(0,0,255),
        CV_RGB(0,128,255),
        CV_RGB(0,255,255),
        CV_RGB(0,255,0),
        CV_RGB(255,128,0),
        CV_RGB(255,255,0),
        CV_RGB(255,0,0),
        CV_RGB(255,0,255)} ;
    Mat gray, smallImg( cvRound (img.rows/scale), cvRound(img.cols/scale), CV_8UC1 );
    
    cvtColor( img, gray, CV_BGR2GRAY );
    resize( gray, smallImg, smallImg.size(), 0, 0, INTER_LINEAR );
    equalizeHist( smallImg, smallImg );
    
    t = (double)cvGetTickCount();
    cascade.detectMultiScale( smallImg, faces,
        1.1, 2, 0
        //|CV_HAAR_FIND_BIGGEST_OBJECT
        //|CV_HAAR_DO_ROUGH_SEARCH
        |CV_HAAR_SCALE_IMAGE
        ,
        Size(30, 30) );
    t = (double)cvGetTickCount() - t;
    
    //printf( "detection time = %g ms\n", t/((double)cvGetTickFrequency()*1000.) );
    for( vector<Rect>::const_iterator r = faces.begin(); r != faces.end(); r++, i++ )
    {
        Mat smallImgROI;
        Point center;
        Scalar color = colors[i%8];
        int radius;
        center.x = cvRound((r->x + r->width*0.5)*scale);
        center.y = cvRound((r->y + r->height*0.5)*scale);
        radius = cvRound((r->width + r->height)*0.25*scale);
        
        cout << center.x << " " << center.y << " " << radius << endl;
    }
    
    return;
}

extern "C" void test1()
{
    cout << "from test" << endl;
}

int main( int argc, const char** argv )
{
    cout << "calling with " << argc << ", " << argv[0] << ", " << argv[1] << endl;
    facedetect(1.0, argv[1], argv[2]);
}

extern "C" void test2(int argc, const char** argv)
{
    cout << "***" << argv[0] << endl;
    cout << "***" << argv[1] << endl;
    main(argc, argv);
}

