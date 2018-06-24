package com.example.zhouge.opencv;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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


    protected static native long[] getTextRect(long inMataddr);


    String result = "";


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
        long matAddrs[] = getTextRect(inMat.nativeObj);
        for (int i = 0; i < matAddrs.length; i++) {
            Mat tmp = new Mat(matAddrs[i]);
            Bitmap tmpBitmap = Bitmap.createBitmap(tmp.width(), tmp.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, tmpBitmap);
            tessBaseAPI.setImage(tmpBitmap);
            tmp.release();
            tmpBitmap.recycle();
            result += tessBaseAPI.getUTF8Text() + "\n";
        }
        inMat.release();
        tessBaseAPI.end();

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent broadcastIntent = new Intent("ocrOver");
        broadcastIntent.putExtra("ocrResult",result);
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
