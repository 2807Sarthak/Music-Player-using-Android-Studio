package com.example.itunes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class AppSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_splash);

        getSupportActionBar().hide();

        Thread thread = new Thread(){
            public void run()
            {
                try {
                    sleep(3000);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(AppSplash.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();

    }
}