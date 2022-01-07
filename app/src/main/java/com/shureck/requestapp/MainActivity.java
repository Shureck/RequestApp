package com.shureck.requestapp;

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

public class MainActivity extends AppCompatActivity {

    private ListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Button add;
    private Button start;
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


        String s = loadText();
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

        mAdapter = new ListAdapter(getApplicationContext(), restaurants);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONArray jsonArray = new JSONArray();

                for(int i=0; i<mRecyclerView.getChildCount(); i++) {
                    View v = mRecyclerView.getChildAt(i);
                    EditText addr = v.findViewById(R.id.addr_edit);
                    EditText port = v.findViewById(R.id.port_edit);
                    EditText time = v.findViewById(R.id.time_edit);

                    JSONObject object = new JSONObject();
                    try {
                        object.put("addr", addr.getText());
                        object.put("port", port.getText());
                        object.put("time", time.getText());

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    jsonArray.put(object);
                    new IOAsyncTask().execute("http://"+addr.getText()+":"+port.getText()+"/");

                }

                String jsonStr = jsonArray.toString();
                saveText(jsonStr);
                System.out.println("jsonString: "+jsonStr);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                restaurants.add(new Req("","",""));

                mAdapter = new ListAdapter(getApplicationContext(), restaurants);
                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(MainActivity.this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        });
    }

//    class RequestTask extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... uri) {
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpResponse response;
//            String responseString = null;
//            try {
//                response = httpclient.execute(new HttpGet(uri[0]));
//                StatusLine statusLine = response.getStatusLine();
//                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    response.getEntity().writeTo(out);
//                    responseString = out.toString();
//                    out.close();
//                } else{
//                    //Closes the connection.
//                    response.getEntity().getContent().close();
//                    throw new IOException(statusLine.getReasonPhrase());
//                }
//            } catch (ClientProtocolException e) {
//                //TODO Handle problems..
//            } catch (IOException e) {
//                //TODO Handle problems..
//            }
//            System.out.println(responseString);
//            return responseString;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            //Do anything with response..
//        }
//    }

//    java.util.Arrays.toString(new int[]{1,2,3,4,5,6,7});
//    JSON.parse(services)
//
    private void saveText(String text) {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT,text);
        ed.commit();
    }
//
    private String loadText() {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
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