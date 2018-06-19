package com.example.zhouge.opencv;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.googlecode.tesseract.android.TessBaseAPI.OEM_DEFAULT;

public class Ocr {

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
    Activity activity;


    public Ocr(Activity activity)
    {
        this.activity=activity;
        //copyToSD(LANGUAGE_PATH, DEFAULT_LANGUAGE_NAME);
        tessBaseAPI=new TessBaseAPI();
        tessBaseAPI.init(DATAPATH, DEFAULT_LANGUAGE,OEM_DEFAULT);
    }


    public String Recognite(Bitmap bitmap)
    {
        return tessBaseAPI.getUTF8Text();
    }









}
