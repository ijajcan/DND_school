package com.example.dndschool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

public class NetworkMonitorService extends Service {
    String networkName = "\"AndroidWifi\"";
    String networkName1 = "\"Ivan\"";
    public static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "NetworkMonitorServiceChannel";
    public static boolean isAppOn = false;
    public ConnectivityManager connectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, Main.class);
        isAppOn = intent.getBooleanExtra("isAppOn", false);
        Log.d("jajcan", String.valueOf(isAppOn));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Network Monitor Service")
                .setContentText("Monitoring network connectivity changes...")
                .setSmallIcon(R.drawable.ic_on)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
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
                if (ssid.equals(networkName) || ssid.equals(networkName1)) {
                    if(isAppOn) {
                        Mute(audioManager);
                        Log.v("jajcan", ssid + "Aveable mute    " + isAppOn);
                    }
                } else {
                    UnMute(audioManager);
                    Log.v("jajcan", ssid + "Aveable unmute  " + isAppOn);
                }
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork == null) {
                    UnMute(audioManager);
                    Log.v("jajcan", "onLost unmute");
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

