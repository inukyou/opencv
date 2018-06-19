package com.example.zhouge.opencv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.FileNotFoundException;

public class MainActivity extends opencvActivity implements CameraBridgeViewBase.CvCameraViewListener2,View.OnClickListener{

    public native String stringFromJNI();
    private static native void getGrayImage(long inMatAddr,long outMatAddr);
    Mat camera_mat;


    private JavaCameraView   javaCameraView;

    Button button_Camera;
    // Used to load the 'native-lib' library on application startup.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        // Example of a call to a native method

        button_Camera=(Button)findViewById(R.id.button_camera);
        button_Camera.setOnClickListener(this);

        javaCameraView=(JavaCameraView)findViewById(R.id.camera_view);
        javaCameraView.setActivity(this);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCameraIndex(0);
        javaCameraView.setClickable(true);

        javaCameraView.enableView();
//        bitmap=((BitmapDrawable)getResources().getDrawable(R.drawable.kikyou)).getBitmap();


    }


    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        camera_mat=inputFrame.rgba();
        Core.rotate(camera_mat,camera_mat,Core.ROTATE_90_CLOCKWISE);
        return camera_mat;
    }


    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.button_camera:{

                Intent intent=new Intent(this,CropImage_Activity.class);

                mat_clon=camera_mat.clone();
                camera_mat.release();
                long addr=mat_clon.nativeObj;
                if(addr!=0&&mat_clon.width()>0&&mat_clon.height()>0){
                    intent.putExtra("mat_addr",addr);
                    startActivity(intent);
                }else
                {
                    Toast.makeText(this,"data_error",Toast.LENGTH_LONG).show();
                }
            }

        }
    }


}
