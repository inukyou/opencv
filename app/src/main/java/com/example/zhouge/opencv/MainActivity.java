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

public class MainActivity extends opencvActivity implements View.OnClickListener{




    Button button;
    // Used to load the 'native-lib' library on application startup.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=(Button)findViewById(R.id.tocamera);
        button.setOnClickListener(this);


    }





    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.tocamera:{

                Intent intent=new Intent(this,CameraActivity.class);
                startActivity(intent);

            }

        }
    }


}
