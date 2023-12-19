package com.example.screenmirror.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import android.window.SplashScreen;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.screenmirror.Manager.AppManager;
import com.example.screenmirror.Manager.AppOpenManager;
import com.example.screenmirror.R;
import com.example.screenmirror.model.AboutModel;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractCollection;
import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    public static String Qureka_Id, ONESIGNAL_APP_ID;
    public static ArrayList<AboutModel> aboutModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        aboutModels = new ArrayList<>();

        startActivity(new Intent(SplashActivity.this, MainActivity.class));

    }


   /* public void getAboutData(){

        //this is for calling api

        StringRequest request = new StringRequest(Request.Method.POST, url_retriveproduct, new Response.Listener<String>() {



            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (success.equals("1")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            int id = object.getInt("id");
                            String name = object.getString("name");
                            String description = object.getString("description");
                            String picture = object.getString("picture");




                          //  Log.e("response",name);
                            aboutModels.add(new AboutModel(id,name,description,picture));

                           // Toast.makeText(SplashActivity.this, "demo"+aboutModels.get(0).getName(), Toast.LENGTH_SHORT).show();
                        }
                        // this will hold activity as per selected time
                        int secondsDelayed = 1;
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                AppManager.StartMAinActivity(SplashActivity.this);
                                finish();
                            }
                        }, secondsDelayed * 2000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("errroror",e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SplashActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }*/
}