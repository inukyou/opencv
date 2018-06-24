package com.example.zhouge.opencv;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;

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
