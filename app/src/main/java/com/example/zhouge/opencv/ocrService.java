package com.example.zhouge.opencv;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ocrService extends IntentService {

    static {
        System.loadLibrary("native-lib");
    }



    private static final String DATAPATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 在DATAPATH中新建这个目录，TessBaseAPI初始化要求必须有这个目录。
     */
    private static final String tessdata = DATAPATH + File.separator + "tessdata";
    /**
     * TessBaseAPI初始化测第二个参数，就是识别库的名字不要后缀名。
     */
    // static final String DEFAULT_LANGUAGE = "chi_sim";
    static final String DEFAULT_LANGUAGE = "chi_sim";
    /**
     * assets中的文件名
     */
    private static final String DEFAULT_LANGUAGE_NAME = DEFAULT_LANGUAGE + ".traineddata";
    /**
     * 保存到SD卡中的完整文件名
     */
    private static final String LANGUAGE_PATH = tessdata + File.separator + DEFAULT_LANGUAGE_NAME;

    private static final String bitmapdata = DATAPATH + File.separator + "tesseract"+ File.separator + "tessdata";

    //private String mFilePath =bitmapdata+File.separator+"image13.jpg";
    //private String mFilePath =DATAPATH + File.separator + "tesseract"+ File.separator + "tessdata"+ File.separator+"image13.jpg";
    private String mFilePath;
    protected static native long[] getTextRect(long inMataddr);

    int weight,height;
    String toresult = "";
    FileOutputStream out;



    public ocrService() {
        super("ocrService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        long addr = intent.getLongExtra("imageMat", 0);
        if (addr == 0)
            return;
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        copyToSD(LANGUAGE_PATH, DEFAULT_LANGUAGE_NAME);
        tessBaseAPI.init(DATAPATH, DEFAULT_LANGUAGE);
        Mat inMat = new Mat(addr);
        Bitmap bitmap=Bitmap.createBitmap(inMat.width(),inMat.height(),Bitmap.Config.ARGB_8888);
        weight=inMat.width();
        height=inMat.width();
        Utils.matToBitmap(inMat,bitmap);
        if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
        {    // 获取SDCard指定目录下
            //String  sdCardDir = Environment.getExternalStorageDirectory()+ "/CoolImage/";
            String  sdCardDir =bitmapdata;
            File dirFile  = new File(sdCardDir);  //目录转化成文件夹
            if (!dirFile .exists()) {              //如果不存在，那就建立这个文件夹
                dirFile .mkdirs();
            }                         //文件夹有啦，就可以保存图片啦
            File file = new File(sdCardDir, "sbimage.jpg");// 在SDcard的目录下创建图片文,以当前时间为其命名
            try {
                out= new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                System.out.println("_________保存到____sd______指定目录文件夹下____________________");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Toast.makeText(HahItemActivity.this,"保存已经至"+Environment.getExternalStorageDirectory()+"/CoolImage/"+"目录文件夹下", Toast.LENGTH_SHORT).show();
        }
        mFilePath=bitmapdata+ File.separator+"sbimage.jpg";


        long matAddrs[] = getTextRect(inMat.nativeObj);
        for (int i = 0; i < matAddrs.length; i++) {
            Mat tmp = new Mat(matAddrs[i]);
            Bitmap tmpBitmap = Bitmap.createBitmap(tmp.width(), tmp.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, tmpBitmap);
            tessBaseAPI.setImage(tmpBitmap);
            tmp.release();
            tmpBitmap.recycle();
            toresult += tessBaseAPI.getUTF8Text() + "\n";
        }
        inMat.release();
        tessBaseAPI.end();

        initAccessToken();
        recognizeResult();


        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent broadcastIntent = new Intent("ocrOver");
        broadcastIntent.putExtra("ocrResult",toresult);
        localBroadcastManager.sendBroadcast(broadcastIntent);
    }

    private void copyToSD(String path, String name) {

        File f = new File(path);
        if (f.exists()) {
            return;
        }
        if (!f.exists()) {
            File p = new File(f.getParent());
            if (!p.exists()) {
                p.mkdirs();
            }
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStream is = null;
        OutputStream os = null;
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

    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                //hasGotToken = true;
            }

            @Override
            public void onError(OCRError ocrError) {
                ocrError.printStackTrace();
                //alertText("licence方式获取token失败", ocrError.getMessage());
            }
        }, getApplicationContext());
        //getApplicationContext生命周期是整个应用
    }

    private void recognizeResult() {
        GeneralBasicParams param = new GeneralBasicParams();
        //设置方向检测
        param.setDetectDirection(true);
        param.setImageFile(new File(mFilePath));
        System.out.println(mFilePath);
        OCR.getInstance(this).recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                StringBuilder stringBuilder = new StringBuilder();
                for (WordSimple wordSimple : result.getWordList()) {
                    stringBuilder.append(wordSimple.getWords());
                    stringBuilder.append("\n");
                }
                //需要解析JSON格式
                if(height<=600) {
                    String s="";
                    toresult = result.getJsonRes();
                    try {
                        JSONObject jobj = new JSONObject(toresult);
                        JSONArray jarray = jobj.getJSONArray("words_result");
                        for (int i = 0; i < jarray.length(); i++) {
                            JSONObject o = jarray.getJSONObject(i);
                            s+=o.getString("words");


                        }
                    } catch (JSONException e) {

                    }

                    toresult=s;
                }

                System.out.println(toresult);
                    System.out.println(weight+"     "+height);
                //}
                //txtResult.setText("识别结果:" + "" + result.getJsonRes());
            }

            @Override
            public void onError(OCRError error) {
                //txtResult.setText(error.getErrorCode() + "" + error.getMessage());
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
