package com.example.jona.shekps;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.nkzawa.engineio.client.Socket;

public class MainActivity extends AppCompatActivity {

    private static MainActivity inst;
    public ProgressDialog pDialog;
    Intent seriviceIntent;
    Notifcation notifcation;

    Context ctx;
    public Context getCtx() {
        return ctx;
    }

    public static MainActivity instance() {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        inst = this;


        ctx = this;

        notifcation = new Notifcation(getCtx());

        seriviceIntent = new Intent(getCtx(), notifcation.getClass());

        if (!isMyServiceRunning(notifcation.getClass())) {

         //   seriviceIntent = new Intent(MainActivity.this, Notifcation.class);

            Log.e("service_log", "service is not running until yet");
            //   Intent i = new Intent(this, MyService.class);
            this.startService(seriviceIntent);
        }
        else {

            Log.e("service_log", "service is already running");
            Check();
        }


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager;
        manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer
                .MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {

       notifcation.alternativeDestroy();

     stopService(seriviceIntent);


            Log.e("service_log", "stopService");



        super.onDestroy();

    }

    public void Check() {

        pDialog.cancel();
        //Notifcation.getInstance().emit("authenticate", "jona");


    }

}
