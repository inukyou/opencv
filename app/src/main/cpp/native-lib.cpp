#include <jni.h>

#include <string>
#include <vector>
#include <android/bitmap.h>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

size_t rowsCount=0;
vector<void *> objectVect;


vector<Rect> GetRowRects(Mat &gray)
{
    vector<Rect>rows;
    int height = gray.rows;
    int *projection = new int[height]();
    Mat src = gray;
    for (int y = 0; y < gray.rows; ++y)
    {
        for (int x = 0; x < gray.cols; ++x)
        {
            /*CvScalar s;
            s = cvGet2D(&src, y, x);
            if (int(s.val[0]) == 255)
                projection[y]++;*/
            uchar val = src.at<uchar>(y,x);
            if((int)val==255)
                projection[y]++;
        }
    }

    bool inLine = false;
    int start = 0;

    for (int i = 0; i < height; i++)
    {
        if (!inLine && projection[i] > 20)
        {
            //由空白进入字符区域了，记录标记
            inLine = true;
            start = i;
        }
        else if ((i - start > 8) && projection[i] < 20 && inLine)
        {
            //由字符区域进入空白区域了
            inLine = false;

            //忽略高度太小的行，比如分隔线
            if (i - start > 20)
            {
                //记录下位置
                Rect rect = Rect(0, start , gray.cols, i - start + 2);
                rows.push_back(rect);
            }
        }
    }
    delete projection;
    return rows;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_zhouge_opencv_CropImage_1Activity_getGrayImage(JNIEnv *env, jclass type,
                                                                jlong inMatAddr, jlong outMatAddr) {

    Mat *inMat=(Mat *)inMatAddr;
    Mat *outMat=(Mat *)outMatAddr;
    cvtColor(*inMat,*outMat,COLOR_BGR2GRAY);
    return;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_example_zhouge_opencv_CropImage_1Activity_getRowsCount(JNIEnv *env, jclass type) {

    return (jint)rowsCount;

}


extern "C"
JNIEXPORT jlongArray JNICALL
Java_com_example_zhouge_opencv_CropImage_1Activity_getTextRect(JNIEnv *env, jclass type,
                                                               jlong inMataddr) {

    Mat *image=(Mat *)inMataddr;
    if (image->rows*image->cols>1000000)
    {
        int t;
        if (image->cols>image->rows)
            t = 1000;
        else
            t = 800;
        resize(*image, *image, Size(t, image->rows*1.0 / image->cols * t), 0, 0, CV_INTER_LINEAR);
    }

    Mat *gray;
    Mat *data=new Mat(image->cols,image->rows,CV_8UC1);
    if(image->channels()>1) {
        gray=new Mat(image->cols,image->rows,CV_8UC1);
        cvtColor(*image, *gray, COLOR_BGR2GRAY);
    }
    else
        gray=image;
    int blockSize = 25;
    int constValue = 10;
    adaptiveThreshold(*gray, *data, 255, ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY_INV, blockSize, constValue);
    vector<Rect>rows = GetRowRects(*data);
    size_t RectsSize=rows.size();
    rowsCount=RectsSize;

    jlongArray matsAddr=env->NewLongArray(RectsSize);
    jlong *addrArr=env->GetLongArrayElements(matsAddr,NULL);



    for(size_t i=0;i<RectsSize;i++)
    {
        Rect rect=rows.at(i);
        Mat *tmp=new Mat(rect.size().width,rect.size().height,CV_8UC1);
        *tmp=(*gray)(rect);
        addrArr[i]=(long)tmp;
    }

    data->release();
    delete(data);
    env->ReleaseLongArrayElements(matsAddr,addrArr,0);
    return matsAddr;


}


extern "C"
JNIEXPORT jlongArray JNICALL
Java_com_example_zhouge_opencv_ocrService_getTextRect(JNIEnv *env, jclass type, jlong inMataddr) {


    Mat *image=(Mat *)inMataddr;
    if (image->rows*image->cols>1000000)
    {
        int t;
        if (image->cols>image->rows)
            t = 1000;
        else
            t = 800;
        resize(*image, *image, Size(t, image->rows*1.0 / image->cols * t), 0, 0, CV_INTER_LINEAR);
    }

    Mat *gray;
    Mat *data=new Mat(image->cols,image->rows,CV_8UC1);
    if(image->channels()>1) {
        gray=new Mat(image->cols,image->rows,CV_8UC1);
        cvtColor(*image, *gray, COLOR_BGR2GRAY);
        objectVect.push_back(gray);
    }
    else
        gray=image;
    int blockSize = 25;
    int constValue = 10;
    adaptiveThreshold(*gray, *data, 255, ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY_INV, blockSize, constValue);
    vector<Rect>rows = GetRowRects(*data);
    size_t RectsSize=rows.size();
    rowsCount=RectsSize;

    jlongArray matsAddr=env->NewLongArray(RectsSize);
    jlong *addrArr=env->GetLongArrayElements(matsAddr,NULL);



    for(size_t i=0;i<RectsSize;i++)
    {
        Rect rect=rows.at(i);
        Mat *tmp=new Mat(rect.size().width,rect.size().height,CV_8UC1);
        *tmp=(*gray)(rect);
        addrArr[i]=(long)tmp;
        objectVect.push_back(tmp);
    }

    data->release();
    delete(data);
    env->ReleaseLongArrayElements(matsAddr,addrArr,0);
    return matsAddr;

}extern "C"
JNIEXPORT void JNICALL
Java_com_example_zhouge_opencv_CropImage_1Activity_releaseAll(JNIEnv *env, jclass type) {

    for(size_t i=objectVect.size()-1;i>=0;i--) {
        delete (objectVect.at(i));
        objectVect.pop_back();
    }

}