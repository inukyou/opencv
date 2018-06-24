package com.example.zhouge.opencv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;


public class opencvActivity extends AppCompatActivity {



    private static final int PERMISSION_REQUEST_CODE=0;

    protected static Mat staticMat;


    private static boolean hasInit=false;

    static {
        System.loadLibrary("native-lib");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题

        requestWindowFeature(Window.FEATURE_NO_TITLE);





        if(!hasInit) {
            hasInit=OpenCVLoader.initDebug();
        }

        if (Build.VERSION.SDK_INT>=23){
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);

            }

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }

        }
    }


}
