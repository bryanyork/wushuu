#include "bgfg_codebook.hpp"
#include "wsnative_export.h"

#include "opencv2/legacy/legacy.hpp"

#include <algorithm>

/**
C Routine to generate object list using the BGS mask
Input: BGS mask and original Image
Output: A list of targets
**/

#define MAXBLOBCNT 200

WSNATIVE_EXPORT bool ProcessFrame(IplImage * detection, IplImage * image, float m_GSD = 20, bgfg_cb_t bgfg_cb = 0)
{
    //too few or too much detection?
    float mask_area = image->width*image->height;
    int width = image->width, height = image->width;
    float det_area = cvCountNonZero(detection);
    if (det_area < mask_area*0.00001 || det_area > mask_area*0.5 )
        return false;
    
    CvMemStorage* storage, *storage1;        
    storage = cvCreateMemStorage(0);
    storage1 = cvCreateMemStorage(0);
    CvSeq* contour;
    
    cvFindContours( detection, storage, &contour, sizeof(CvContour), CV_RETR_TREE, CV_CHAIN_APPROX_NONE);

    CvRect rectBlob[MAXBLOBCNT];
    CvBox2D rectBlob2D[MAXBLOBCNT];

    int nBlobNum = 0;

    
    //note: this line erase all detected foreground pixels
    cvZero(detection);

    //go over all the blobs
    for( ; contour != 0; contour = contour->h_next )
    {
        double fContourArea = fabs(cvContourArea(contour, CV_WHOLE_SEQ ));
        CvRect tmpRect = cvBoundingRect(contour, 1);
        float fRatio = 1.0*tmpRect.height/tmpRect.width;
        float occupy = fContourArea/(tmpRect.height*tmpRect.width);

        //TODO: make it an option
        cvDrawContours(detection,contour,CV_RGB(255,255,255),CV_RGB(255,255,255),0,CV_FILLED,8);
        
        //get the GSD(Ground Sampling Distance) for that location
        float GSD = 20;
        /*
        if (tmpRect.height/2.0 +tmpRect.y < m_Y1 )
            GSD = m_GSD1;
        else if (tmpRect.height/2.0 +tmpRect.y > m_Y2 )
            GSD = m_GSD2;
        else
            GSD = 1.0*m_GSD1*(m_Y2 - tmpRect.y - tmpRect.height/2.0 )/(m_Y2-m_Y1)+ 1.0*m_GSD2*(tmpRect.height/2.0 +tmpRect.y -m_Y1)/(m_Y2-m_Y1);
        */
        
        CvRect tmpROI = cvBoundingRect( contour, 1);
        float boundary = 3; //pixels
        float min_area = GSD*GSD*0.10 /*0.25 for regular*/, max_area = width*height;
        
        if ( ( (tmpROI.x >= boundary && tmpROI.x+tmpROI.width <= width-boundary) || fContourArea > std::min<float>(1600.0 ,max_area/36) ) && 
             fContourArea<max_area && fContourArea >= min_area && 
             !( (occupy < 0.5 &&  fContourArea/(GSD*GSD) > 4.0 ) || occupy <0.33 ) && //TODO: make it adaptive?
            nBlobNum< MAXBLOBCNT)
        {
            rectBlob[nBlobNum] = tmpROI;

            CvBox2D box2d = cvFitEllipse2( contour);
            rectBlob2D[nBlobNum] = box2d;

            nBlobNum++;

            //draw outline
            //cvDrawContours(showimage,contour,CV_RGB(255,255,255),CV_RGB(255,255,255),1,1,8);
        }
    }

    for (int j = 0; j< nBlobNum; j++)
    {
    //get the attributes of each blob here
        if(bgfg_cb) {
            const CvRect& r = rectBlob[j];
            bgfg_cb(r.x, r.y, r.width, r.height);
        }
    }

    cvReleaseMemStorage( &storage); 
    cvReleaseMemStorage( &storage1); 

    return true;
}
