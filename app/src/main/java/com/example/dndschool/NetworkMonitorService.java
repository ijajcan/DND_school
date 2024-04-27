package com.example.dndschool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class NetworkMonitorService extends Service {
    public static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "NetworkMonitorServiceChannel";
    public static boolean isAppOn = false;
    public static ArrayList<String> wifiList = new ArrayList<>();
    public ConnectivityManager connectivityManager;
    public static final String ACTION_UPDATE_WIFI_LIST = "com.example.dndschool.ACTION_UPDATE_WIFI_LIST";

    @Override
    public void onCreate() {
        super.onCreate();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_UPDATE_WIFI_LIST.equals(action)) {
                ArrayList<String> updatedWifiList = intent.getStringArrayListExtra("wifiList");
                if (updatedWifiList != null) {
                    wifiList.clear();
                    wifiList.addAll(updatedWifiList);
                }
            }
        }

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, Main.class);
        isAppOn = intent.getBooleanExtra("isAppOn", false);
        if (intent.getStringArrayListExtra("wifiList") != null) {
            wifiList = intent.getStringArrayListExtra("wifiList");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Network Monitor Service")
                .setContentText("Monitoring network connectivity changes...")
                .setSmallIcon(R.drawable.ic_on)
                .setContentIntent(pendingIntent)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        registerNetworkCallback();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Network Monitor Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void registerNetworkCallback() {
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = wifiInfo.getSSID();
                if (ssid.length() > 2 && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length() - 1).trim(); // Remove first and last character
                }

                Log.e("jajcan", ssid);
                Log.e("jajcan", wifiList.toString());
                Log.e("jajcan", String.valueOf(wifiList.contains(ssid)));
                if (wifiList.contains(ssid)) {
                    if(isAppOn) {
                        Mute(audioManager);
                    }
                } else {
                    UnMute(audioManager);
                }
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork == null) {
                    UnMute(audioManager);
                }
            }
        };
        connectivityManager.registerNetworkCallback(request, callback);
    }

    public void Mute(AudioManager audioManager) {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    public static void UnMute(AudioManager audioManager) {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    @Override
    public void onDestroy() {
        Log.e("jajcan","service stoped");
        super.onDestroy();
    }
}
