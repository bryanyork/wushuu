// DecodeStream.cpp : Defines the entry point for the console application.
//

#include <pthread.h>

#include <stdio.h>
#include <unistd.h>

#include "HCNetSDK.h"
#include "PlayM4.h"

#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc_c.h"
#include "opencv2/imgproc/imgproc.hpp"


#define MR(Y,U,V) (Y + (1.403)*(V-128))  
#define MG(Y,U,V) (Y - (0.344) * (U-128) - (0.714) * (V-128) )   
#define MB(Y,U,V) (Y + ((1.773) * (U-128)))  

void YUV420_C_RGB( char* pYUV, unsigned char* pRGB, int height, int width)  
{
    char* pY = pYUV;  
    char* pU = pYUV+height*width;  
    char* pV = pU+(height*width/4);  
    
    unsigned char* pBGR = NULL;  
    unsigned char R = 0;  
    unsigned char G = 0;  
    unsigned char B = 0;  
    char Y = 0;  
    char U = 0;  
    char V = 0;  
    double tmp = 0;  
    for ( int i = 0; i < height; ++i )  
    {  
        for ( int j = 0; j < width; ++j )  
        {  
            pBGR = pRGB+ i*width*3+j*3;  
            
            Y = *(pY+i*width+j);  
            U = *pU;  
            V = *pV;  
            
            //B  
            tmp = MB(Y, U, V);
            B = (unsigned char)tmp;  
            //G  
            tmp = MG(Y, U, V);  
            G = (unsigned char)tmp;  
            //R  
            tmp = MR(Y, U, V);
            R = (unsigned char)tmp;  
            
            *pBGR     = R;              
            *(pBGR+1) = G;          
            *(pBGR+2) = B;  
            
            if ( i%2 == 0 && j%2 == 0)  
            {  
                *pU++;
            }  
            else  
            {  
                if ( j%2 == 0 )
                {  
                    *pV++;
                }  
            }  
        }  
      
    }  
}


void CALLBACK g_DecodeCallback(int nPort, char* pBuf, int nSize, FRAME_INFO* pFrameInfo, int nUser, int nReserved1)
{
    printf("thread %d: in decode callback, buf size %d, frame (type %d, width %d, height %d, fps %d, stamp %d)\n", pthread_self(), nSize, pFrameInfo->nType, pFrameInfo->nWidth, pFrameInfo->nHeight, pFrameInfo->nFrameRate, pFrameInfo->nStamp);
    static int seq = 1;
    if(T_YV12 == pFrameInfo->nType)
    {
#if 0
        unsigned char* pRGB = NULL;  
        pRGB = (unsigned char*)malloc(nSize*sizeof(unsigned char*)*2);
        YUV420_C_RGB(pBuf, pRGB, pFrameInfo->nWidth, pFrameInfo->nHeight);
        IplImage *image;  
        image = cvCreateImageHeader(cvSize(pFrameInfo->nWidth, pFrameInfo->nHeight),IPL_DEPTH_8U,3);  
        cvSetData(image,pRGB,pFrameInfo->nWidth*2);

        cv::Mat m = image;
//#else
        IplImage *image,*rgbimg,*yimg,*uimg,*vimg,*uuimg,*vvimg;
        rgbimg = cvCreateImage(cvSize(pFrameInfo->nWidth, pFrameInfo->nHeight), IPL_DEPTH_8U, 3);
        image = cvCreateImage(cvSize(pFrameInfo->nWidth, pFrameInfo->nHeight), IPL_DEPTH_8U, 3);
        yimg = cvCreateImageHeader(cvSize(pFrameInfo->nWidth, pFrameInfo->nHeight), IPL_DEPTH_8U, 1);
        uimg = cvCreateImageHeader(cvSize(pFrameInfo->nWidth/2, pFrameInfo->nHeight/2), IPL_DEPTH_8U, 1);
        vimg = cvCreateImageHeader(cvSize(pFrameInfo->nWidth/2, pFrameInfo->nHeight/2), IPL_DEPTH_8U, 1);
        uuimg = cvCreateImage(cvSize(pFrameInfo->nWidth, pFrameInfo->nHeight), IPL_DEPTH_8U, 1);
        vvimg = cvCreateImage(cvSize(pFrameInfo->nWidth, pFrameInfo->nHeight), IPL_DEPTH_8U, 1);
        cvSetData(yimg, pBuf, pFrameInfo->nWidth);
        cvSetData(uimg, pBuf+pFrameInfo->nWidth*pFrameInfo->nHeight, pFrameInfo->nWidth/2);
        cvSetData(vimg, pBuf+long(pFrameInfo->nWidth*pFrameInfo->nHeight*1.25), pFrameInfo->nWidth/2);
        cvResize(uimg,uuimg,CV_INTER_LINEAR);
        cvResize(vimg,vvimg,CV_INTER_LINEAR);
        cvMerge(yimg,uuimg,vvimg,NULL,image);
        cvCvtColor(image,rgbimg,CV_YCrCb2BGR);
        cv::Mat m = image;
#else

#if 0
        cv::Mat m;
        cv::cvtColor(m1,m2,CV_YUV2RGB_YV12);
#else
        cv::Mat m1(cv::Size(pFrameInfo->nWidth, pFrameInfo->nHeight*3/2), CV_8UC1, pBuf);
        printf("++++++ rows %d, cols %d,  channels %d, depth %d, step %d\n", m1.rows, m1.cols, m1.channels(), m1.depth(), (int)m1.step);
        //cv::Mat m2(cv::Size(pFrameInfo->nWidth, pFrameInfo->nHeight), CV_8UC3);
        cv::Mat m2;
        cv::cvtColor(m1,m2,CV_YUV2BGR_YV12,3);
        ++seq;
        char fn[1024];
        snprintf(fn, sizeof(fn), "./m1-%d.jpg", seq);
        cv::imwrite(fn, m1);
        snprintf(fn, sizeof(fn), "./m2-%d.jpg", seq);
        cv::imwrite(fn, m2);
#endif

#endif
    }
}

void CALLBACK g_RealDataCallBack(LONG lRealHandle, DWORD dwDataType, BYTE *pBuffer,DWORD dwBufSize,DWORD dwUser)
{
    printf("thread %d: get data type %d, size %d.\n", pthread_self(), dwDataType, dwBufSize);
    static int m4Port = -1;

    switch(dwDataType)
    {
        case NET_DVR_SYSHEAD:
            if(-1 == m4Port)
            {
                if(!PlayM4_GetPort(&m4Port))
                {
                    printf("GetPort failed\n");
                    return;
                }
                if(!PlayM4_SetStreamOpenMode(m4Port, STREAME_REALTIME))
                {
                    printf("SetStreamOpenMode failed\n");
                    return;
                }
                if(!PlayM4_OpenStream(m4Port, pBuffer, dwBufSize, 1024*1024))
                {
                    printf("OpenStream failed\n");
                    return;
                }
                if(!PlayM4_SetDecCallBackMend(m4Port, g_DecodeCallback, 0))
                {
                    printf("SetDecCallBackMend failed\n");
                    return;
                }
                if(!PlayM4_Play(m4Port, 0))
                {
                    printf("Play failed\n");
                    return;
                }
            }
            break;
        case NET_DVR_STREAMDATA:
            if(!PlayM4_InputData(m4Port, pBuffer, dwBufSize))
            {
                printf("InputData failed\n");
                return;
            }
            break;
        case NET_DVR_AUDIOSTREAMDATA:
            break;
        case NET_DVR_PRIVATE_DATA:
            break;
        default:
            break;
    }
}

void CALLBACK g_HikDataCallBack(LONG lRealHandle, DWORD dwDataType, BYTE *pBuffer,DWORD dwBufSize,DWORD dwUser)
{
    printf("pyd---(private)Get data,the size is %d.\n", dwBufSize);
}

void CALLBACK g_StdDataCallBack(int lRealHandle, unsigned int dwDataType, unsigned char *pBuffer, unsigned int dwBufSize, unsigned int dwUser)
{
    printf("pyd---(rtsp)Get data,the size is %d.\n", dwBufSize);
}

int main(int argc, const char* argv[])
{
    printf("main thread id %d\n", pthread_self());
    NET_DVR_Init();
    long lUserID;
    //login
    NET_DVR_DEVICEINFO struDeviceInfo;
    lUserID = NET_DVR_Login("192.168.2.216", 8000, "admin", "12345", &struDeviceInfo);
    //lUserID = NET_DVR_Login("192.168.2.210", 8000, "admin", "12345", &struDeviceInfo);
    if (lUserID < 0)
    {
        printf("login---error, %d\n", NET_DVR_GetLastError());
        return -1;
    }

    //Set callback function of getting stream.
    long lRealPlayHandle;
    NET_DVR_CLIENTINFO ClientInfo = {0};
    ClientInfo.hPlayWnd     = 0;

    ClientInfo.lChannel     = 1;  //channel NO
    //ClientInfo.lLinkMode  = 0x40000000; //Record when breaking network.
    ClientInfo.lLinkMode  = 0;
    //ClientInfo.byPreviewMode    = 0;
    //ClientInfo.dwStreamType    = 0;
    ClientInfo.sMultiCastIP = NULL;

    lRealPlayHandle = NET_DVR_RealPlay_V30(lUserID, &ClientInfo);
    if (lRealPlayHandle < 0)
    {
        printf("realplay---error, %d\n", NET_DVR_GetLastError());
        NET_DVR_Logout(lUserID);
        NET_DVR_Cleanup();
        return -2;
    }

    //Set callback function of getting stream.
    int iRet;
    iRet = NET_DVR_SetRealDataCallBack(lRealPlayHandle, g_RealDataCallBack, 0);
    if (!iRet)
    {
        printf("set data cb---error, %d\n", NET_DVR_GetLastError());
        NET_DVR_StopRealPlay(lRealPlayHandle);
        NET_DVR_Logout(lUserID);
        NET_DVR_Cleanup();
        return -3;
    }

    if (!NET_DVR_MakeKeyFrame(lUserID, 1))
    {
        printf("--NET_DVR_MakeKeyFrame error\n");
        NET_DVR_StopRealPlay(lRealPlayHandle);
        NET_DVR_Logout(lUserID);
        NET_DVR_Cleanup();
        return -4;
    }

    sleep(5);   //second

    //stop
    NET_DVR_StopRealPlay(lRealPlayHandle);
    NET_DVR_Logout(lUserID);
    NET_DVR_Cleanup();

	return 0;
}

extern "C" void test_jna()
{
  main(0, 0);
}

