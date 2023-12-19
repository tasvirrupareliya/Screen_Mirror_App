package com.example.screenmirror.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
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

public class ScreenMirroringActivity extends AppCompatActivity {

    public Dialog dialog;
    private int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_mirroring);

        NetworkStateManager.getInstance().getNetworkConnectivityStatus()
                .observe(this, activeNetworkStateObserver);


        // this is global class and claa global method
        AppManager.ChangeStatusBarColor(ScreenMirroringActivity.this);

        findViewById(R.id.am_cvScreenCast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                number = 2;
                /* JK524 */
                 showInterstitial();

            }
        });

        findViewById(R.id.am_cvWebBrowser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* JK524 */
                if (AppManager.IsWifiOn) {
                    // RewardDialog();
                    AppManager.StartConnectionActivity(ScreenMirroringActivity.this);
                } else {
                    WifiTurnOnDialog();
                }
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = 3;
                showInterstitial();
            }
        });

        findViewById(R.id.play_games).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent("android.intent.action.VIEW").setData(Uri.parse(SplashActivity.Qureka_Id));
                startActivity(intent);

            }
        });


        findViewById(R.id._info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                number = 4;
                /* JK524 */
                showInterstitial();
                if (number == 1) {

                    if (AppManager.IsWifiOn) {
                        RewardDialog();
                    } else {
                        WifiTurnOnDialog();
                    }

                } else if (number == 2) {
                    startActivity(new Intent("android.settings.CAST_SETTINGS"));
                } else if (number == 3) {
                    startActivity(new Intent(ScreenMirroringActivity.this, SettingActivity.class));
                } else if (number == 4) {
                    AppManager.StartTutorialActivity(ScreenMirroringActivity.this);
                }
            }
        });

        findViewById(R.id.rl_wifi_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
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
            startActivity(new Intent(ScreenMirroringActivity.this, SettingActivity.class));
        } else if (number == 4) {
            AppManager.StartTutorialActivity(ScreenMirroringActivity.this);
        }
    }

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
        WifiStatus(AppManager.isNetworkAvailable(ScreenMirroringActivity.this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        WifiStatus(AppManager.isNetworkAvailable(ScreenMirroringActivity.this));
    }

    private void WifiTurnOnDialog() {
        dialog = new Dialog(ScreenMirroringActivity.this);
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

        dialog = new Dialog(ScreenMirroringActivity.this);
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

}