package com.example.screenmirror.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.screenmirror.Constants;
import com.example.screenmirror.Manager.AppManager;
import com.example.screenmirror.R;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private String[] quality = new String[]{"HD","Medium","Low"};
    private BroadcastReceiver _localListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleLocalBroadcast(intent);
        }
    };
    private LocalBroadcastManager _localBroadcaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initLocalBroadcaster();
        //call to local brodcaster and update its setting
        _localBroadcaster.sendBroadcast(new Intent(Constants.SETTING_REQUEST));

        findViewById(R.id.rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateus();
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String body = "Best video and status downloader app....DOWNLOAD NOW....";
                String sub = "http://play.google.com/store/apps/details?id="+getPackageName();
                intent.putExtra(Intent.EXTRA_TEXT, body);
                intent.putExtra(Intent.EXTRA_TEXT, sub);
                startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });


        findViewById(R.id.more_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent("android.intent.action.VIEW").setData(Uri.parse("https://play.google.com/store/apps/developer?id=VOODOO"));
                startActivity(intent);
            }
        });

        TextView textView = findViewById(R.id.version);
        try {
            textView.setText(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        findViewById(R.id.txt_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent("android.intent.action.VIEW").setData(Uri.parse("https://play.google.com/store/apps/datasafety?id=com.studiosoolter.screenmirroring.miracast.apps"));
                startActivity(intent);
            }
        });

        AppManager.ChangeStatusBarColor(SettingActivity.this);

        spinner = findViewById(R.id.Spinner);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,quality);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if(i==0){
            saveAll("100");
        }else if(i==1){
            saveAll("75");
        }else if(i==2) {
            saveAll("50");
        }else {
            saveAll("100");
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    //this is rate us dialog
    public void rateus() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.rate_us_dailog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.rating_bar);
        dialog.findViewById(R.id.txtnotnow).setOnClickListener(new View.OnClickListener() { // from class: m.a.a.b.w
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                Dialog dialog2 = dialog;
                dialog2.dismiss();
            }
        });
        dialog.findViewById(R.id.txtsubmit).setOnClickListener(new View.OnClickListener() { // from class: m.a.a.b.c0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {

                dialog.dismiss();
                Intent  intent = new Intent("android.intent.action.VIEW").setData(Uri.parse("http://play.google.com/store/apps/details?id=com.studiosoolter.screenmirroring.miracast.apps"));
                startActivity(intent);
            }
        });
        dialog.show();
    }



    public void saveAll(String quality){
        Intent toSend = new Intent(Constants.SETTING_CHANGED_EVENT);
        toSend.putExtra(Constants.SETTING_SERVER_PORT, "8080");
        toSend.putExtra(Constants.SETTING_JPEG_QUALI, quality);
        _localBroadcaster.sendBroadcast(toSend);
    }


    private void handleLocalBroadcast(Intent rawIntent) {
        String action = rawIntent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case Constants.SETTING_INFO_EVENT:
                break;
            default:
                return;
        }
    }


    private void initLocalBroadcaster() {
        if (_localBroadcaster == null) {
            _localBroadcaster = LocalBroadcastManager.getInstance(getApplicationContext());
            _localBroadcaster.registerReceiver(_localListener, getIntentFilter());
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter toReturn = new IntentFilter(Constants.SETTING_INFO_EVENT);
        return toReturn;
    }

}