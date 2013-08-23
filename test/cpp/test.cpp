#include "opencv2/opencv.hpp"

#include <time.h>

using namespace cv;
using namespace std;

FileStorage create_fs(const string& name, int flags) {
    FileStorage fs(name, flags);

    fs << "frameCount" << 5;
    time_t rawtime; time(&rawtime);
    fs << "calibrationDate" << asctime(localtime(&rawtime));
    Mat cameraMatrix = (Mat_<double>(3,3) << 1000, 0, 320, 0, 1000, 240, 0, 0, 1);
    Mat distCoeffs = (Mat_<double>(5,1) << 0.1, 0.01, -0.001, 0, 0);
    fs << "cameraMatrix" << cameraMatrix << "distCoeffs" << distCoeffs;
    fs << "features" << "[";
    for( int i = 0; i < 3; i++ )
    {
        int x = rand() % 640;
        int y = rand() % 480;
        uchar lbp = rand() % 256;

        fs << "{" << "x" << x << "y" << y << "lbp" << "[";
        for( int j = 0; j < 8; j++ )
            fs << ((lbp >> j) & 1);
        fs << "]" << "}";
    }
    fs << "]";
    return fs;
}

int main(int, char** argv)
{
    FileStorage fs = FileStorage("test.yml", FileStorage::WRITE);
    Mat m = imread(argv[1]);
    fs << "jpg" << m;
    fs.release();

    return 0;
}

typedef void (*test_cb_t)(const char* buf);

test_cb_t g_test_cb = 0;

extern "C" {

void set_cb(test_cb_t cb) {
    g_test_cb = cb;
}

void test_cb() {
    FileStorage fs = create_fs(".yml", FileStorage::WRITE|FileStorage::MEMORY);
    string yml = fs.releaseAndGetString();

    g_test_cb(yml.c_str());
}

}
