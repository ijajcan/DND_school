package com.example.dndschool;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

public class Main extends AppCompatActivity {
    Switch onOff;
    TextView indikacija;
    public static boolean isAppOn = false;
    public static final String SHARED_PREFERENCES = "SharedPreferences";
    public static final String SWITCH = "switch";
    boolean permission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onOff = findViewById(R.id.onOff);
        indikacija = findViewById(R.id.indikacija);
        AskForPremission(Main.this);


        if(permission) {
            Load();
//            StartService();
        }
        Log.v("jajcan", "main LOaded");
    }

    public void SetOnOff(View view) {
        isAppOn = !isAppOn;
        Intent serviceIntent = new Intent(this, com.example.dndschool.NetworkMonitorService.class);
        if (isAppOn) {
            indikacija.setText("Automatski utišaj mobitel kada je sppojen na Wifi mrežu eduroam");
            AskForPremission(Main.this);
            serviceIntent.putExtra("isAppOn", isAppOn);
            startService(serviceIntent);
        } else {
            indikacija.setText("Nemoj automatski utišavati mobitel");
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            stopService(serviceIntent);
        }
        Save();
    }

    public void AskForPremission(Context context) {
        if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS}, 101);
        } else {
            permission = true;
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
                new AlertDialog.Builder(Main.this)
                        .setTitle("Potrebna Dozvola")
                        .setMessage("Ova aplikacija treba pristup obavijestima. Želite li nastaviti?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
        }


        if (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Show the dialog
            new AlertDialog.Builder(Main.this)
                    .setTitle("Potrebna Dozvola")
                    .setMessage("Ova aplikacija treba pristup vašoj lokaciji. Želite li nastaviti?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            permission = false;
        } else {
            permission = true;
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        if (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Show the dialog
            new AlertDialog.Builder(Main.this)
                    .setTitle("Potrebna Dozvola")
                    .setMessage("Ova aplikacija treba pristup vašoj trenutnoj lokaciji. Želite li nastaviti?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            permission = false;
        } else {
            permission = true;
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        }



//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 3);
//            permission = false;
//        } else {
//            permission = true;
//            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        }
    }
    public void Save() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCH, isAppOn);
        editor.apply();
    }

    public void Load() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        isAppOn = sharedPreferences.getBoolean(SWITCH, false);
        onOff.setChecked(isAppOn);
        if (isAppOn) {
            indikacija.setText("Automatski utišaj mobitel kada je sppojen na Wifi mrežu eduroam");
        } else {
            indikacija.setText("Nemoj automatski utišavati mobitel");
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }
}