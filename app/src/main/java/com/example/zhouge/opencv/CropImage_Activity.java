package com.example.zhouge.opencv;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class CropImage_Activity extends opencvActivity implements View.OnClickListener {


    private static native void getGrayImage(long inMatAddr,long outMatAddr);

    Mat mat;
    CropImageView cropImageView;
    Button button_crop,button_cannel;
    Bitmap cropBitMap;
    Dialog dia;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image_);

        textView=(TextView)findViewById(R.id.recognitetext);
        cropImageView=(CropImageView)findViewById(R.id.cropimageview);
        button_crop=(Button)findViewById(R.id.crop_button);
        button_cannel=(Button)findViewById(R.id.button_cannel);

        button_crop.setOnClickListener(this);
        button_cannel.setOnClickListener(this);

        long matAddr=getIntent().getLongExtra("mat_addr",0);
        if(matAddr==0)
            Toast.makeText(this,"data_null",Toast.LENGTH_LONG).show();
        else
        {
            mat=new Mat(matAddr);
            Bitmap bitmap=Bitmap.createBitmap(mat.width(),mat.height(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat,bitmap);
            mat.release();
            cropImageView.setDrawable(bitmap,bitmap.getWidth(),bitmap.getHeight());

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.crop_button:{
                cropBitMap=cropImageView.getCropImage();
                Mat m=new Mat();
                Utils.bitmapToMat(cropBitMap,mat);
                getGrayImage(mat.nativeObj,mat.nativeObj);
                Bitmap grayBitMap=Bitmap.createBitmap(mat.width(),mat.height(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat,grayBitMap);

                dia = new Dialog(this, R.style.edit_AlertDialog_style);
                ImageView imageView=new ImageView(this);
                imageView.setImageBitmap(grayBitMap);
                dia.setCanceledOnTouchOutside(true);
                dia.setContentView(imageView);
                dia.show();
                Ocr ocr=new Ocr(this);

                final String s=ocr.Recognite(grayBitMap);
                Toast.makeText(this,s,Toast.LENGTH_LONG).show();
                textView.setText(s);
                break;
            }
            case R.id.button_cannel:{

                finish();
            }
        }
    }
}
