#include <jni.h>

#include <string>
#include <android/bitmap.h>
#include <opencv2/opencv.hpp>

using namespace cv;


//jobject mat_to_bitmap(JNIEnv * env, Mat & src, bool needPremultiplyAlpha, jobject bitmap_config);
jobject GrayImag(JNIEnv *env, jclass type,jobject bmpObj);

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_zhouge_opencv_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_zhouge_opencv_MainActivity_getGrayImage(JNIEnv *env, jclass type,jlong inMatAddr,jlong outMatAddr) {
    Mat *inMat=(Mat *)inMatAddr;
    Mat *outMat=(Mat *)outMatAddr;
    cvtColor(*inMat,*outMat,COLOR_BGR2GRAY);
    return;
}

/*
jobject mat_to_bitmap(JNIEnv * env, Mat & src, bool needPremultiplyAlpha, jobject bitmap_config){
    jclass java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetStaticMethodID(java_bitmap_class,
                                           "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");

    jobject bitmap = env->CallStaticObjectMethod(java_bitmap_class,
                                                 mid, src.size().width, src.size().height, bitmap_config);
    AndroidBitmapInfo  info;
    void*              pixels = 0;

    try {
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        CV_Assert(src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        CV_Assert(pixels);
        if(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888){
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if(src.type() == CV_8UC1){
                cvtColor(src, tmp, CV_GRAY2RGBA);
            }else if(src.type() == CV_8UC3){
                cvtColor(src, tmp, CV_RGB2RGBA);
            }else if(src.type() == CV_8UC4){
                if(needPremultiplyAlpha){
                    cvtColor(src, tmp, COLOR_RGBA2mRGBA);
                }else{
                    src.copyTo(tmp);
                }
            }
        }else{
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if(src.type() == CV_8UC1){
                cvtColor(src, tmp, CV_GRAY2BGR565);
            }else if(src.type() == CV_8UC3){
                cvtColor(src, tmp, CV_RGB2BGR565);
            }else if(src.type() == CV_8UC4){
                cvtColor(src, tmp, CV_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return bitmap;
    }catch(cv::Exception e){
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return bitmap;
    }catch (...){
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return bitmap;
    }
}*/

extern "C"
JNIEXPORT void JNICALL
Java_com_example_zhouge_opencv_CropImage_1Activity_getGrayImage(JNIEnv *env, jclass type,jlong inMatAddr,jlong outMatAddr) {
    Mat *inMat=(Mat *)inMatAddr;
    Mat *outMat=(Mat *)outMatAddr;
    cvtColor(*inMat,*outMat,COLOR_BGR2GRAY);
    return;
}

/*
jobject GrayImag(JNIEnv *env, jclass type,jobject bmpObj)
{
    AndroidBitmapInfo bmpInfo={0};
    if(AndroidBitmap_getInfo(env,bmpObj,&bmpInfo)<0)
        return nullptr;
    void* pixels =NULL;
    if(AndroidBitmap_lockPixels(env,bmpObj,&pixels)<0)
        return nullptr;
    Mat m(bmpInfo.height,bmpInfo.width,CV_8UC4,pixels);

    Mat *dst=new Mat();
    cvtColor(m,*dst,COLOR_BGR2GRAY);

    AndroidBitmap_unlockPixels(env,bmpObj);
    return _bitmap;
}*/