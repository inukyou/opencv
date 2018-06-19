package com.example.zhouge.opencv;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.googlecode.tesseract.android.TessBaseAPI.OEM_DEFAULT;

public class CropImage_Activity extends opencvActivity implements View.OnClickListener {


    private static final String DATAPATH = Environment.getExternalStorageDirectory().getAbsolutePath() ;
    /** * 在DATAPATH中新建这个目录，TessBaseAPI初始化要求必须有这个目录。 */
    private static final String tessdata = DATAPATH + File.separator+ "tessdata";
    /** * TessBaseAPI初始化测第二个参数，就是识别库的名字不要后缀名。 */
    // static final String DEFAULT_LANGUAGE = "chi_sim";
    static final String DEFAULT_LANGUAGE = "chi_sim";
    /** * assets中的文件名 */
    private static final String DEFAULT_LANGUAGE_NAME = DEFAULT_LANGUAGE + ".traineddata";
    /** * 保存到SD卡中的完整文件名 */
    private static final String LANGUAGE_PATH = tessdata +File.separator + DEFAULT_LANGUAGE_NAME;
    TessBaseAPI tessBaseAPI;


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

        copyToSD(LANGUAGE_PATH, DEFAULT_LANGUAGE_NAME);
        tessBaseAPI=new TessBaseAPI();
        tessBaseAPI.init(DATAPATH, DEFAULT_LANGUAGE);

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
                tessBaseAPI.setImage(grayBitMap);
                final String s=tessBaseAPI.getUTF8Text();
                Toast.makeText(this,s,Toast.LENGTH_LONG).show();
                textView.setText(s);
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
        tessBaseAPI.end();
    }

    private void copyToSD(String path, String name)
    {


//如果存在就删掉
        File f = new File(path);
        if (f.exists()){
            return;
        }
        if (!f.exists()){
            File p = new File(f.getParent());
            if (!p.exists()){
                p.mkdirs();
            }
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStream is=null;
        OutputStream os=null;
        try {
            is = getAssets().open(name);
            File file = new File(path);
            os = new FileOutputStream(file);
            byte[] bytes = new byte[2048];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
