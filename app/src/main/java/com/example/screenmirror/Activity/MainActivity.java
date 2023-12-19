package com.example.screenmirror.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.screenmirror.Manager.AppManager;
import com.example.screenmirror.R;
import com.example.screenmirror.model.NetworkStateManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {

    public Dialog dialog;
    private int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkStateManager.getInstance().getNetworkConnectivityStatus()
                .observe(this, activeNetworkStateObserver);
        AppManager.ChangeStatusBarColor(MainActivity.this);

        findViewById(R.id.am_cvScreenCast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                number = 2;
                startActivity(new Intent(MainActivity.this, ScreenMirroringActivity.class));
            }
        });

        findViewById(R.id.am_cvHowToConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = 4;
                showInterstitial();
            }
        });

        findViewById(R.id.am_cvSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = 3;
                showInterstitial();

            }
        });

        findViewById(R.id.am_cvAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

        findViewById(R.id.play_games).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent("android.intent.action.VIEW").setData(Uri.parse(SplashActivity.Qureka_Id));
                startActivity(intent);

            }
        });


        findViewById(R.id.rl_wifi_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
            }
        });

    }

    //check wifi status is connected or not
    @SuppressLint("ResourceAsColor")
    public void WifiStatus(boolean IsNetwork) {
        AppManager.IsWifiOn = IsNetwork;
        if (IsNetwork) {
            findViewById(R.id.rl_wifi_status).setBackgroundTintList(getResources().getColorStateList(R.color.Green));
            TextView textView = findViewById(R.id.wifi_status);
            textView.setText("connected");

        } else {
            findViewById(R.id.rl_wifi_status).setBackgroundTintList(getResources().getColorStateList(R.color.Red));
            TextView textView = findViewById(R.id.wifi_status);
            textView.setText("Disconnected");
        }
    }

    private final Observer<Boolean> activeNetworkStateObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean isConnected) {

            Log.e("data", "data" + isConnected);
            WifiStatus(isConnected);

            if (isConnected) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        WifiStatus(AppManager.isNetworkAvailable(MainActivity.this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        WifiStatus(AppManager.isNetworkAvailable(MainActivity.this));
    }

    //dialog box for show user that device is not connected with wifi
    private void WifiTurnOnDialog() {
        dialog = new Dialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.wifi_status_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dialog.findViewById(R.id.txt_turn_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
            }
        });

        dialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void RewardDialog() {

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.reward_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dialog.findViewById(R.id.ic_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });
    }


    // this is back pressed events

    @Override
    public void onBackPressed() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetDialogTheme);
        View bottom = LayoutInflater.from(MainActivity.this).inflate(R.layout.exit_dailog, null);
        bottomSheetDialog.setContentView(bottom);
        bottomSheetDialog.show();

        Button exit = bottom.findViewById(R.id.exit);
        Button notnow = bottom.findViewById(R.id.notnow);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();
                finishAffinity();
                System.exit(0);
            }
        });
        notnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

    }


    private void showInterstitial() {
        if (number == 1) {
            if (AppManager.IsWifiOn) {
                RewardDialog();
            } else {
                WifiTurnOnDialog();
            }

        } else if (number == 2) {
            startActivity(new Intent("android.settings.CAST_SETTINGS"));
        } else if (number == 3) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        } else if (number == 4) {
            AppManager.StartTutorialActivity(MainActivity.this);
        }
    }

}

