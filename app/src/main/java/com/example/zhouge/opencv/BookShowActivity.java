package com.example.zhouge.opencv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class BookShowActivity extends AppCompatActivity {


    WebView webView;
    TextView textView;

    int id=99999;
    String bookname="",author="",publishName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshow);

        textView=(TextView)findViewById(R.id.bookinfoview);
        id=getIntent().getIntExtra("id",999);
        bookname=getIntent().getStringExtra("name");
        author=getIntent().getStringExtra("author");
        publishName=getIntent().getStringExtra("publish");


        textView.setText("id:  "+id+"\nname:  "+bookname+"\nauthor: "+author+"\npublish: "+publishName);

        webView=(WebView)findViewById(R.id.webview);
        webView.loadUrl("http://39.108.83.161:8080/image/"+id+".jpg");


    }
}
