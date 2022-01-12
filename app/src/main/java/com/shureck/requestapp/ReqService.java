package com.shureck.requestapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

public class ReqService extends Service {

    Timer myTimer = new Timer();
    SharedPreferences sPref;
    final String SAVED_TEXT = "saved_info";
    private String jsonName = "MyPref";
    long sum = 0;
    long delay = 0;
    private final OkHttpClient client = new OkHttpClient();
    private static final int ID_SERVICE = 101;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String s = loadText(jsonName);
        JSONArray objArr = null;
        delay = Long.valueOf(loadText("period"));
        System.out.println("Delay "+delay);
        sum = 0;

        sendingReq(s);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(ID_SERVICE, notification);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myTimer.cancel();
    }

    private String loadText(String name) {
        sPref = getSharedPreferences(name, MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_TEXT, "");
        return savedText;
    }

    class IOAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            System.out.println("2222222222 "+params[0]);
            return sendData(params[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("TAG",response);
            System.out.println("AAAAA0000000 "+response);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_MIN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    private void sendingReq(String s){
        JSONArray objArr = null;

        try {
            objArr = new JSONArray(s);
            for (int i=0; i<objArr.length(); i++) {
                JSONObject obj = new JSONObject(objArr.get(i).toString());
                myTimer.schedule(new TimerTask() { // Определяем задачу
                    @Override
                    public void run() {
                        try {
                            System.out.println(new Req(obj.getString("addr"),obj.getString("port"),obj.getString("time")).toString());
                            System.out.println("LLLLL "+ Long.valueOf(obj.getString("delayTime")));
                            new IOAsyncTask().execute("http://"+obj.getString("addr")+":"+obj.getString("port")+"/");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, Long.valueOf(obj.getString("delayTime")) * 1000, delay * 60L * 1000); //todo выставление правильных промежутков времени, использование Period
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String sendData(String str){
        try {
            Request request = new Request.Builder()
                    .url(str)
                    .get()
                    //.post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
