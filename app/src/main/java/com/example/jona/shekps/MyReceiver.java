package com.example.jona.shekps;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.jona.shekps.Notifcation;

public class MyReceiver  extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, Notifcation.class));
            Log.e("service_log","startedService from Receiver");
        }

    }
