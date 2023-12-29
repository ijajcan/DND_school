package com.example.dndschool;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
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
    public static final String SVITCH = "svitch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onOff = findViewById(R.id.onOff);
        indikacija = findViewById(R.id.indikacija);
        AskForPremission(Main.this);
        Load();
        StartService();
    }

    public void StartService() {
        Intent serviceIntent = new Intent(this, com.example.dndschool.NetworkMonitorService.class);
        serviceIntent.putExtra("isAppOn", isAppOn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
    public void SetOnOff(View view) {
        isAppOn = !isAppOn;
        if (isAppOn) {
            indikacija.setText("Upaljeno");
            AskForPremission(Main.this);
        } else {
            indikacija.setText("Ugašeno");
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
        Save();
        // Update the service with the new isAppOn value
        Intent serviceIntent = new Intent(this, com.example.dndschool.NetworkMonitorService.class);
        serviceIntent.putExtra("isAppOn", isAppOn);
        startService(serviceIntent);
    }

    public void AskForPremission(Context context) {
        if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS}, 101);
        } else {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
        } else {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
    }

    public void Save() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SVITCH, isAppOn);
        editor.apply();
        Log.v("jajcan", "saved");
    }

    public void Load() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        isAppOn = sharedPreferences.getBoolean(SVITCH, false);
        onOff.setChecked(isAppOn);
        if (isAppOn) {
            indikacija.setText("Upaljeno");
        } else {
            indikacija.setText("Ugašeno");
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }
}

