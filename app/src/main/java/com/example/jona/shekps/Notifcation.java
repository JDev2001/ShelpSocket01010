package com.example.jona.shekps;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;


public class Notifcation extends Service {

    private String user_id = "jona";


    //create object of class
    public static Notifcation Single;

    //----------------------------interface object------------------//
    private oneventlitsener mCallback;

    //Socket object
    Socket socket;
    private Context context;
    //socket connection status
    public boolean socket_status = false;

    //constructor
    public Notifcation(Context applicationContext) {
        super();

    }

    public Notifcation() {
    }

    //-------
    //-------------
    //----------------interface for sending data from server to
    // activity--------------------------//
    public interface oneventlitsener {

        public void setData(String data, String tag);
    }

    //---------------------------------get context from activity to send data to
    // activity----------------------------//
    public void setContext(Context c) {
        this.context = c;
        mCallback = (oneventlitsener) context;
    }

    /* Static 'instance' method */
    public static Notifcation getInstance() {
        return Single;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Single = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // user_id = intent.getStringExtra("user_id");
        runSocket();


        //restart Service when it killed
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.e("serivce_log", "Service OnDestroy");
        Intent broadcastIntent = new Intent("start.den.service.neu");
        sendBroadcast(broadcastIntent);
        Single = null;
        socket_status = false;
        stopSelf();

    }

    public void alternativeDestroy() {
        // super.onDestroy();
        //    Log.e("serivce_log","Service OnDestroy");
        //   Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition
        // .RestartSensor");
        //    sendBroadcast(broadcastIntent);

    }


    //---------***********------------------Run socket for listening to various
    // event-----*******------//
    public void runSocket() {

        //check if socket connected or not
        if (!socket_status) {
            try {
                IO.Options opts = new IO.Options();
                opts.reconnection = true;//try to reconnect socket when it disconnected from server
                opts.reconnectionDelay = 0;//reconnection delay time in milliseconds
                opts.reconnectionAttempts = 5;//no of attempts to reconnect socket
                //opts.forceNew = true;
                socket = IO.socket("murxl", opts);//server ip address

                //Methods for listening to events.
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.d("socket id", socket.id());
                        Log.d("Socket Status", "socket connected");
                        socket_status = true;
                        //call main activity method to tell socket connected
                        MainActivity mainActivity = MainActivity.instance();

                        String name = "jona_" + new Date().getTime();
                        emit("authenticate", name);
                        Log.e("socket_log", name);


                        if (mainActivity != null) {
                            mainActivity.Check();

                        }

                    }

                }).on("send_response", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {

                        //***********------------------send data from service to
                        // activity----------------********//
                        try {

                            mCallback.setData(args[0].toString(), "send_response");
                            System.out.println("send_response=======>" + args[0].toString());
                        }
                        catch (Exception e) {

                            System.out.println("send_response=======>" + e.toString());
                        }
                    }
                }).on("notification", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        try {
                            //***********------------------Generate
                            // notification----------------********//

                            callBroad(args[0].toString());
                            Log.e("socket_log", args[0].toString());
                            System.out.println("notification=======>" + args[0].toString());
                        }
                        catch (Exception e) {
                            System.out.println("notification=======>" + e.toString());
                        }
                    }
                }).on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.e("EVENT_RECONNECT_FAILED", "yes");
                        socket_status = false;
                        Log.e("Socket Status", "socket disconnected");

                    }

                }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.e("EVENT_RECONNECT", "yes");
                        socket_status = true;
                        Log.e("Socket Status", "socket reconnected");

                    }

                }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.e("EVENT_CONNECT_TIMEOUT", "yes");

                    }

                }).on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.e("EVENT_RECONNECT_ATTEMPT", "yes");

                    }

                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.e("EVENT_DISCONNECT", "yes");
                    }

                });
                socket.connect();


                // socket.emit("authenticate","jona");

            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        else {
            //call main activity method to tell socket already connected
            MainActivity mainActivity = MainActivity.instance();
            if (mainActivity != null) {
                mainActivity.Check();
            }
        }
    }

    //*********-----------send data to server-----********//
    public void emit(String key, JSONObject data) {
        socket.emit(key, data);

        System.out.println(key + "@@@@@@@@@ emitting");
    }

    public void emit(String key, String data) {
        socket.emit(key, data);
        System.out.println(key + "@@@@@@@@@ emitting");
    }

    public void emit(String key, JSONArray data) {
        socket.emit(key, data);

        System.out.println(key + "@@@@@@@@ emitting");
    }

    //call broadcast receiver to generate notifications
    public void callBroad(String data) {

        //  Intent intent = new Intent(this, SocketApp.class);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification n = new Notification.Builder(this).setContentText(data).setSmallIcon(R
                .drawable.ic_launcher_background).setContentIntent(pendingIntent).build();

        startForeground(1, n);
        Log.e("socket_log", "noti");


    /*    PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis
    (), MainActivity.instance().getIntent(), 0);



        Notification n  = new Notification.Builder(this)
                .setContentText(data)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pIntent).build();



      NotificationManager notificationManager = (NotificationManager) getSystemService
      (NOTIFICATION_SERVICE);
       n.flags |= Notification.FLAG_AUTO_CANCEL;

       notificationManager.notify(0, n);

*/

    }

}
