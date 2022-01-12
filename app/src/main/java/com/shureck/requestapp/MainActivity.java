package com.shureck.requestapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.rtt.RangingRequest;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ListAdapter.OnBtnClickListener {

    private ListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Button add;
    private Button start;
    private Button stop;
    private ImageButton clearButton;
    private EditText period_time;
    private String jsonName = "MyPref";
    public ArrayList<Req> restaurants = new ArrayList<>();
    SharedPreferences sPref;
    final String SAVED_TEXT = "saved_info";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        add = findViewById(R.id.add_btn);
        start = findViewById(R.id.req_start);
        stop = findViewById(R.id.stop_button);
        period_time = findViewById(R.id.period_time);
        clearButton = findViewById(R.id.clearButton);

        String s = loadText(jsonName);
        System.out.println("AAAAAA "+s);
        JSONArray objArr = null;
        JSONObject obj = null;
        try {
            objArr = new JSONArray(s);
            for (int i=0; i<objArr.length(); i++) {
                obj = new JSONObject(objArr.get(i).toString());
                restaurants.add(new Req(obj.getString("addr"),obj.getString("port"),obj.getString("time")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter = new ListAdapter(getApplicationContext(), restaurants, this);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        period_time.setText(loadText("period"));

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONArray jsonArray = new JSONArray();
                long sum = 0;
                for(int i=0; i<mRecyclerView.getChildCount(); i++) {
                    View v = mRecyclerView.getChildAt(i);
                    EditText addr = v.findViewById(R.id.addr_edit);
                    EditText port = v.findViewById(R.id.port_edit);
                    EditText time = v.findViewById(R.id.time_edit);
                    sum += Long.valueOf(String.valueOf(time.getText()));
                    JSONObject object = new JSONObject();
                    try {
                        object.put("addr", addr.getText());
                        object.put("port", port.getText());
                        object.put("time", time.getText());
                        object.put("delayTime", sum);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    jsonArray.put(object);

                }

                String jsonStr = jsonArray.toString();
                saveText(jsonName,jsonStr);
                System.out.println("jsonString: "+jsonStr);

                saveText("period", String.valueOf(period_time.getText()));

                startForegroundService(new Intent(MainActivity.this, ReqService.class).putExtra("period_time", Long.valueOf(String.valueOf(period_time.getText()))));
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, ReqService.class));
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                restaurants.add(new Req("","",""));
                mAdapter.notifyItemInserted(restaurants.size()-1);

                long sum = 0;
                JSONArray jsonArray = new JSONArray();
                for(int i=0; i<mRecyclerView.getChildCount(); i++) {
                    View v = mRecyclerView.getChildAt(i);
                    EditText addr = v.findViewById(R.id.addr_edit);
                    EditText port = v.findViewById(R.id.port_edit);
                    EditText time = v.findViewById(R.id.time_edit);
                    if (String.valueOf(time.getText()).length() > 0) {
                        sum += Long.valueOf(String.valueOf(time.getText()));
                    }
                    else {
                        sum += 0;
                    }
                    JSONObject object = new JSONObject();
                    try {
                        object.put("addr", addr.getText());
                        object.put("port", port.getText());
                        object.put("time", time.getText());
                        object.put("delayTime", sum);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    jsonArray.put(object);
                }
                String jsonStr = jsonArray.toString();
                saveText(jsonName,jsonStr);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long sum = 0;
                JSONArray jsonArray = new JSONArray();
                for(int i=0; i<mRecyclerView.getChildCount(); i++) {
                    View v = mRecyclerView.getChildAt(i);
                    EditText addr = v.findViewById(R.id.addr_edit);
                    EditText port = v.findViewById(R.id.port_edit);
                    EditText time = v.findViewById(R.id.time_edit);

                    addr.setText("");
                    port.setText("");
                    time.setText("");
                }
                for(int i=0; i<mRecyclerView.getChildCount(); i++) {
                    View v = mRecyclerView.getChildAt(i);
                    EditText addr = v.findViewById(R.id.addr_edit);
                    EditText port = v.findViewById(R.id.port_edit);
                    EditText time = v.findViewById(R.id.time_edit);
                    if (String.valueOf(time.getText()).length() > 0) {
                        sum += Long.valueOf(String.valueOf(time.getText()));
                    }
                    else {
                        sum += 0;
                    }
                    JSONObject object = new JSONObject();
                    try {
                        object.put("addr", addr.getText());
                        object.put("port", port.getText());
                        object.put("time", time.getText());
                        object.put("delayTime", sum);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    jsonArray.put(object);
                }
                String jsonStr = jsonArray.toString();
                saveText(jsonName,jsonStr);
            }
        });
    }


    private void saveText(String name, String text) {
        sPref = getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT,text);
        ed.commit();
    }
//
    private String loadText(String name) {
        sPref = getSharedPreferences(name, MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_TEXT, "");
        return savedText;
    }

    @Override
    public void onDeleteBtnClick(int position) {
        restaurants.remove(position);
        mAdapter.notifyItemRemoved(position);
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