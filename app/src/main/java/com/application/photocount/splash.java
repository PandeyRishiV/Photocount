package com.application.photocount;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

public class splash extends AppCompatActivity {
    RelativeLayout splasher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splasher = findViewById(R.id.splash);

        //splash screen hold
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(splash.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },1500);
    }
}
