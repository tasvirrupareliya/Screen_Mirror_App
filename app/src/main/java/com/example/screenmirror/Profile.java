package com.example.screenmirror;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.screenmirror.Activity.SplashActivity;
import com.example.screenmirror.utility.ZoomableImageView;

public class Profile extends AppCompatActivity {
    ZoomableImageView images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Intent intent = getIntent();

        images = findViewById(R.id.image);
        if(intent.getIntExtra("key",0)==0){

            Glide.with(this).load(SplashActivity.aboutModels.get(0).getPicture()).into(images);
        }

        if(intent.getIntExtra("key",0)==1){

            Glide.with(this).load(SplashActivity.aboutModels.get(1).getPicture()).into(images);
        }

        if(intent.getIntExtra("key",0)==2){

            Glide.with(this).load(SplashActivity.aboutModels.get(2).getPicture()).into(images);
        }




    }
}