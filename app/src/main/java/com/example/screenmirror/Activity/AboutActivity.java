package com.example.screenmirror.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.screenmirror.Manager.AppManager;
import com.example.screenmirror.Manager.AppOpenManager;
import com.example.screenmirror.Profile;
import com.example.screenmirror.R;
import com.example.screenmirror.databinding.ActivityAboutBinding;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class AboutActivity extends AppCompatActivity {

    // this is for binding
     ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //click events
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //set data into text which got from api
        binding.keyurTxt.setText(SplashActivity.aboutModels.get(0).getName());
        binding.tasvirTxt.setText(SplashActivity.aboutModels.get(1).getName());
        binding.jigarTxt.setText(SplashActivity.aboutModels.get(2).getName());

        binding.keyurDes.setText(SplashActivity.aboutModels.get(0).getDescription());
        binding.tasvirDes.setText(SplashActivity.aboutModels.get(1).getDescription());
        binding.jigarDes.setText(SplashActivity.aboutModels.get(2).getDescription());


        //this is for load image url into imageview
        Glide.with(this).load(SplashActivity.aboutModels.get(0).getPicture()).into(binding.keyur);
        Glide.with(this).load(SplashActivity.aboutModels.get(1).getPicture()).into(binding.tasvir);
        Glide.with(this).load(SplashActivity.aboutModels.get(2).getPicture()).into(binding.jigar);

        binding.keyur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AboutActivity.this, Profile.class);
                intent.putExtra("key",0);
                startActivity(intent);
            }
        });

        binding.tasvir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AboutActivity.this, Profile.class);
                intent.putExtra("key",1);
                startActivity(intent);
            }
        });

        binding.jigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity.this, Profile.class);
                intent.putExtra("key",2);
                startActivity(intent);
            }
        });




    }

}