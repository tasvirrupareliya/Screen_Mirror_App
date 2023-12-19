package com.example.screenmirror.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.screenmirror.BackgroundService;
import com.example.screenmirror.Capturing.CaptureService;
import com.example.screenmirror.Constants;
import com.example.screenmirror.HTTP_Server.ServerService;
import com.example.screenmirror.Manager.AppManager;
import com.example.screenmirror.R;
import com.example.screenmirror.Settings.SettingsService;

public class ConnectionActivity extends AppCompatActivity {
    private DrawerLayout _DrawerLayout;

    private boolean _IsHttpServerRunning_shadow = false;
    private String _HttpServerPort_shadow = "" + Constants.DEFAULT_HTTP_SERVER_PORT;
    private String _IpAddress_shadow = "";

    private LocalBroadcastManager _localBroadcaster;
    private BroadcastReceiver _localListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleLocalBroadcast(intent);
        }
    };


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        setContentView(R.layout.activity_connection);
        checkAndAskForInternetPermission();

        AppManager.ChangeStatusBarColor(ConnectionActivity.this);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //click events
        findViewById(R.id.btn_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(ConnectionActivity.this, MainActivity.class);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });

        //start local brodcaster
        initLocalBroadcaster();
        startService(new Intent(getApplicationContext(), ServerService.class));
        startService(new Intent(getApplicationContext(), SettingsService.class));
        startService(new Intent(getApplicationContext(), CaptureService.class));

        //update ip address and request for port
        requestIpUpdate();
        requestPortUpdate();

        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                TextView textView = findViewById(R.id.txt_ip);
                String sub = textView.getText().toString();
                intent.putExtra(Intent.EXTRA_TEXT, sub);
                startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                TextView textView = findViewById(R.id.txt_ip);
                ClipData clip = ClipData.newPlainText("data", textView.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ConnectionActivity.this, "Text Copied!", Toast.LENGTH_SHORT).show();

            }
        });


        initStartButton();
    }

    //start web services using httpserver
    private void startHttpServer() {
        if (_localBroadcaster != null) {
            Intent toSend = new Intent(Constants.SERVER_HTTP_EVENT_NAME_COMMAND);
            toSend.putExtra(Constants.SERVER_HTTP_COMMAND, Constants.SERVER_HTTP_START);
            _localBroadcaster.sendBroadcast(toSend);
        }
        requestIpUpdate();
    }

    //stop web services using httpserver
    private void stopHttpServer() {
        if (_localBroadcaster != null) {
            Intent toSend = new Intent(Constants.SERVER_HTTP_EVENT_NAME_COMMAND);
            toSend.putExtra(Constants.SERVER_HTTP_COMMAND, Constants.SERVER_HTTP_STOP);
            _localBroadcaster.sendBroadcast(toSend);
        }
    }

    private void initStartButton() {
        TextView startButton = findViewById(R.id.buttonStart);
        startButton.setText(R.string.buttonStart);
        View.OnClickListener listenerToSet = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectionActivity.this._IsHttpServerRunning_shadow) {
                    Toast.makeText(ConnectionActivity.this, "Server get Stopped", Toast.LENGTH_SHORT).show();
                    ConnectionActivity.this.stopHttpServer();
                    ConnectionActivity.this.stopCapturing();
                    return;
                }
                Toast.makeText(ConnectionActivity.this, "Server get Started", Toast.LENGTH_SHORT).show();
                ConnectionActivity.this.startHttpServer();
                ConnectionActivity.this.TryToStartCaptureService();
            }
        };
        startButton.setOnClickListener(listenerToSet);
    }

    private void stopCapturing() {
        if (_localBroadcaster != null) {
            Intent toSend = new Intent(Constants.CAPTURE_EVENT_NAME_COMMAND);
            toSend.putExtra(Constants.CAPTURE_COMMAND, Constants.CAPTURE_STOP);
            _localBroadcaster.sendBroadcast(toSend);
        }
        requestIpUpdate();
    }

    private void updateDisplayedValuesOn() {
        TextView ipInfo = findViewById(R.id.yourIPText);
        ipInfo.setText(getString(R.string.ipInfoServerOn));

        TextView ipAddressText = findViewById(R.id.ip);
        TextView ipAddressText_one = findViewById(R.id.txt_ip);
        String URI = _IpAddress_shadow + ":" + _HttpServerPort_shadow;
        ipAddressText.setText(URI);
        ipAddressText_one.setText(URI);

        TextView button = findViewById(R.id.buttonStart);
        button.setText(R.string.buttonStop);

    }

    private void updateDisplayedValuesOff() {
        TextView ipInfo = findViewById(R.id.yourIPText);
        ipInfo.setText(R.string.ipInfoServerOff);

        TextView ipAddressText = findViewById(R.id.ip);
        TextView ipAddressText_one = findViewById(R.id.txt_ip);
        ipAddressText.setText(_IpAddress_shadow + "");
        ipAddressText_one.setText(_IpAddress_shadow + "");

        TextView button = findViewById(R.id.buttonStart);
        button.setText(R.string.buttonStart);

    }

    /**
     * Can only used if localBroadcastManager is active.
     */
    private void requestIpUpdate() {
        if (_localBroadcaster != null) {
            Intent temp = new Intent(Constants.IP_REQUEST);
            _localBroadcaster.sendBroadcast(temp);
        } else {
            if (Constants.InDebugging) {
                Log.i("LocalBroadcast Main", "Illegal State access");
            }
        }
    }

    private void handleIpUpdate(Intent rawIntent) {
        String addr = rawIntent.getStringExtra(Constants.IP_ANSWER_ADDRESS);
        Log.e("data", addr);
        String isRunning = rawIntent.getStringExtra(Constants.IP_ANSWER_FLAG_RUN);

        if (addr != null && isRunning != null) {
            _IpAddress_shadow = addr;
            if (isRunning == Constants.SERVER_HTTP_IS_RUNNING_TRUE) {
                _IsHttpServerRunning_shadow = true;
                updateDisplayedValuesOn();
            } else if (isRunning == Constants.SERVER_HTTP_IS_RUNNING_FALSE) {
                _IsHttpServerRunning_shadow = false;
                updateDisplayedValuesOff();
            }
        }
    }

    /**
     * Can only used if localBroadcastManager is active.
     */
    private void requestPortUpdate() {
        if (_localBroadcaster != null) {
            Intent temp = new Intent(Constants.SETTING_REQUEST);
            _localBroadcaster.sendBroadcast(temp);
        } else {
            if (Constants.InDebugging) {
                Log.i("LocalBroadcast Main", "Illegal State access");
            }
        }
    }

    private void handlePortChange(Intent rawIntent) {
        String port = "8080";
        if (port != null && port != _HttpServerPort_shadow) {
            _HttpServerPort_shadow = port;
            Log.e("data", port);
            if (_IsHttpServerRunning_shadow) {
                updateDisplayedValuesOn();
            } else {
                updateDisplayedValuesOff();
            }
        }
    }

    private void handleImageChange(Intent rawIntent) {
        byte[] data = rawIntent.getByteArrayExtra(Constants.IMAGE_DATA_NAME);
        if (data != null) {
            if (_IsHttpServerRunning_shadow) {
                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                // ImageView view = findViewById(R.id.imagePreview);
                // view.setImageBitmap(image);
            } else {
                //resetImageAndQr();
            }
        }
    }

    private void handleLocalBroadcast(Intent rawIntent) {
        String action = rawIntent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case Constants.IP_ANSWER:
                handleIpUpdate(rawIntent);
                break;
            case Constants.IMAGE_EVENT_NAME:
                handleImageChange(rawIntent);
                break;
            case Constants.QR_CODE_EVENT:
                setQRImage(rawIntent);
                break;
            case Constants.SETTING_INFO_EVENT:
                handlePortChange(rawIntent);
                break;
            case Constants.SETTING_CHANGED_EVENT:
                handlePortChange(rawIntent);
                break;
            case Constants.CLIENT_CONNECTED_EVENT:
                updateConnectedClients(rawIntent);
                break;
            default:
                return;
        }
    }

    //this is for set IntentFilters
    private IntentFilter getIntentFilter() {
        IntentFilter toReturn = new IntentFilter(Constants.IMAGE_EVENT_NAME);
        toReturn.addAction(Constants.IP_ANSWER);
        toReturn.addAction(Constants.QR_CODE_EVENT);
        toReturn.addAction(Constants.SETTING_INFO_EVENT);
        toReturn.addAction(Constants.SETTING_CHANGED_EVENT);
        toReturn.addAction(Constants.CLIENT_CONNECTED_EVENT);
        return toReturn;
    }

    private synchronized void setQRImage(Intent data) {
        Parcelable p = data.getParcelableExtra(Constants.QR_CODE_DATA);
        Bitmap b = (Bitmap) p;
        //  ImageView imageView = findViewById(R.id.QRImage);
        //imageView.setImageBitmap(b);
    }

// this is for internet permission

    private void checkAndAskForInternetPermission() {
        //Prüfe auf Permissions für Internet
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (Constants.InDebugging) {
                Log.i("Main", "Keine Perms");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, Constants.INTERNET_PERMISSION);
        } else {
            if (Constants.InDebugging) {
                Log.i("Main", "Hat Perms");
            }
        }
    }


    // this code is for start capturing screen
    private void TryToStartCaptureService() {
        MediaProjectionManager temp = null;
        //Result checking in callback methode onActivityResult()
        try {
            if (Constants.InDebugging) {
                Log.d("BeforeServiceStart", "Now trying to get Mediamanager.");
            }
            temp = (MediaProjectionManager) getSystemService(getApplicationContext().MEDIA_PROJECTION_SERVICE);
            startActivityForResult(temp.createScreenCaptureIntent(), Constants.REQUEST_CODE_SCREEN_CAPTURE);
        } catch (Exception ex) {
            if (Constants.InDebugging) {
                Log.d("BeforeServiceStart", ex.getMessage());
            }
        }
    }

    //it will update connection status
    private void updateConnectedClients(Intent intent) {
        Log.i("MainActivity", "UpdateClientAnzahl");
        // TextView tv = findViewById(R.id.connectedClients);
        int amount = intent.getIntExtra(Constants.CLIENT_AMOUNT, -1);
        String temp = getString(R.string.connected_clients) + " " + amount;
        // tv.setText(temp);
    }

    private void initLocalBroadcaster() {
        if (_localBroadcaster == null) {
            _localBroadcaster = LocalBroadcastManager.getInstance(getApplicationContext());
            _localBroadcaster.registerReceiver(_localListener, getIntentFilter());
        }
    }

    private void cleanLocalBroadcaster() {
        if (_localBroadcaster != null) {
            _localBroadcaster.unregisterReceiver(_localListener);
            _localBroadcaster = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initLocalBroadcaster();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanLocalBroadcaster();
    }

    //this will return intent result

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_SCREEN_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent toSend = new Intent(Constants.CAPTURE_EVENT_NAME_COMMAND);
                toSend.putExtra(Constants.CAPTURE_COMMAND, Constants.CAPTURE_INIT);
                toSend.putExtra(Constants.CAPTURE_MEDIA_GRANTING_TOKEN_INTENT, data);
                _localBroadcaster.sendBroadcast(toSend);
               // Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
            } else {
                if (Constants.InDebugging) {
                    Log.d("BeforeServiceStarting", "Request failed.");
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                _DrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}


