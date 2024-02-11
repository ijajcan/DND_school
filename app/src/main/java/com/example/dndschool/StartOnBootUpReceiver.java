package com.example.dndschool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class StartOnBootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Get the value of isAppOn from SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences(Main.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            boolean isAppOn = sharedPreferences.getBoolean(Main.SWITCH, false);

            // Start your MainActivity
//            Intent activityIntent = new Intent(context, Main.class);
//            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(activityIntent);

            // Start your service
            Intent serviceIntent = new Intent(context, NetworkMonitorService.class);
            serviceIntent.putExtra("isAppOn", isAppOn);  // Pass isAppOn as an extra
            context.startService(serviceIntent);
        }
    }
}

