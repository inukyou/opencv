package com.example.zhouge.opencv;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import org.json.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class HttpService extends IntentService {

    OkHttpClient okHttpClient;
    ArrayList<BookInfo> bookList;


    public HttpService()
    {
        super("HttpService");
        okHttpClient=new OkHttpClient();
    }

    public HttpService(String name) {
        super(name);
        okHttpClient=new OkHttpClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        bookList=new ArrayList<>();
        String ocrResult=intent.getStringExtra("ocrResult");
        if(ocrResult==null||ocrResult.equals(""))
            sendMess();
        String result="";
        try {
            RequestBody requestBody=new FormBody.Builder().add("ocrResult",ocrResult).build();
            Request request=new Request.Builder().url("http://192.168.1.106:8080/test/FirstServlet").post(requestBody).build();
            Response response=okHttpClient.newCall(request).execute();
            result=response.body().string();
            bookList=BookInfo.JSONtoBookInfoList(result);

        } catch (IOException e) {
            result=null;
        }

    }

    private void sendMess()
    {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent broadcastIntent = new Intent("HttpOver");
        broadcastIntent.putExtra("bookList",(Serializable)bookList);
        localBroadcastManager.sendBroadcast(broadcastIntent);
    }
}
