package com.example.zhouge.opencv;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CropImage_Activity extends opencvActivity implements View.OnClickListener {

    protected static native void getGrayImage(long inMatAddr,long outMatAddr);
    protected static native long[] getTextRect(long inMataddr);
    protected static native void releaseAll();
    ArrayList<BookInfo> bookList=null;

    String ocrResult="";
    String HttpResult="";


    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/imagenet_comp_graph_label_strings.txt";

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Activity activity;

    LocalBroadcastManager localBroadcastManager;
    OCRReceiver ocrReceiver;
    HttpReceiver httpReceiver;
    MaterialDialog.Builder waitDialog,ocrDialog,httpDialog;
    MaterialDialog dialog;

    String result2="";
    String result3="";


    CropImageView cropImageView;
    Button button_crop,button_cannel;
    Bitmap cropBitMap;
    Bitmap bitmap;


    TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image_);

        activity=this;


        textView2=(TextView)findViewById(R.id.classfytext);
        cropImageView=(CropImageView)findViewById(R.id.cropimageview);
        button_crop=(Button)findViewById(R.id.crop_button);
        button_cannel=(Button)findViewById(R.id.button_cannel);

        button_crop.setOnClickListener(this);
        button_cannel.setOnClickListener(this);



        Mat mat=staticMat.clone();
        bitmap=Bitmap.createBitmap(mat.width(),mat.height(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat,bitmap);
        mat.release();
        cropImageView.setDrawable(bitmap,bitmap.getWidth(),bitmap.getHeight());


        regist();
        waitDialog=new MaterialDialog.Builder(this)
                .title("")
                .content("处理中")
                .progress(true, 0)
                .cancelable(false);

        dialog=waitDialog.build();
        httpDialog=new MaterialDialog.Builder(this)
                .title("查询结果")
                .positiveText("确定")
                .negativeText("取消");

         ocrDialog= new MaterialDialog.Builder(this)
                .title("识别结果")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent serviceIntent=new Intent(activity,HttpService.class);//启动http后台服务
                        serviceIntent.putExtra("ocrResult",ocrResult);//将ocrResult传
                        //serviceIntent.putExtra("imageMat",m.nativeObj);
                        dialog.show();
                        startService(serviceIntent);
                    }
                });
        initTensorFlowAndLoadModel();

    }

    @Override
    public void onClick(View view) {
        String s="";
        switch (view.getId())
        {
            case R.id.crop_button:{

                cropBitMap=cropImageView.getCropImage();
                Mat m=new Mat(cropBitMap.getWidth(),cropBitMap.getHeight(), CvType.CV_8UC3);
                Utils.bitmapToMat(cropBitMap,m);
                //getGrayImage(mat.nativeObj,mat.nativeObj);

                Intent serviceIntent=new Intent(this,ocrService.class);
                serviceIntent.putExtra("imageMat",m.nativeObj);
                startService(serviceIntent);
                dialog.show();
                break;
            }
            case R.id.button_cannel:{

                finish();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(ocrReceiver);
    }



    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button_cannel.setVisibility(View.VISIBLE);
            }
        });
    }

    private void regist() {

        IntentFilter ocrIntentFilter = new IntentFilter();
        ocrIntentFilter.addAction("ocrOver");
        ocrReceiver =new OCRReceiver();

        IntentFilter httpIntentFilter = new IntentFilter();
        httpIntentFilter.addAction("HttpOver");
        httpReceiver =new HttpReceiver();

        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(ocrReceiver,ocrIntentFilter);
        localBroadcastManager.registerReceiver(httpReceiver,httpIntentFilter);
    }


    private class OCRReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            dialog.cancel();
            dialog.dismiss();
            ocrResult=intent.getStringExtra("ocrResult");
            Bitmap bitmapten = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmapten);
            result2="\n物体识别结果："+results.toString()+"\n";
            result3=result2;
            ocrDialog.content(ocrResult+result3);
            ocrDialog.show();
            //releaseAll();
        }
    }


    private class HttpReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            HttpResult=intent.getStringExtra("httpResult");
            bookList=(ArrayList<BookInfo>) intent.getSerializableExtra("bookList");
            if(HttpResult==null)
                HttpResult="null";

            httpDialog.adapter(new BookAdapter(bookList,activity),new LinearLayoutManager(activity));
            dialog.cancel();
            dialog.dismiss();
            httpDialog.show();
        }
    }

}
