package com.example.eadproject.QueueHandler;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.eadproject.R;
import com.example.eadproject.FuelHandler.ShowFuelInfo;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eadproject.SQLHelper.SQLHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*
 *  Queue total class
 * */
public class ShowQueueTotal extends AppCompatActivity {

    private Cursor cursor;
    private Button insertButton,infoButton;
    private RequestQueue requestQueue;
    private String  id,vehicleType, fuel, city, email, station,QueueId ;
    private EditText total, average;
    private SQLiteDatabase liteDB;
    SQLHelper database;
    String res = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_length_queue);

        infoButton = findViewById(R.id.btnViewQueueDataViewFuelDetails);
        total = findViewById(R.id.txtViewQueueDataQueueLength);
        insertButton = findViewById(R.id.btnViewQueueDataEnter);
        average = findViewById(R.id.txtViewQueueAverageTime);

        city = getIntent().getStringExtra("city");
        email = getIntent().getStringExtra("email");
        fuel = getIntent().getStringExtra("fuelType");
        id = getIntent().getStringExtra("id");
        station = getIntent().getStringExtra("station");

        database = new SQLHelper(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        handleSSLHandshake();

        //get average queue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://192.168.1.5:44323/api/queue/vehicleQueue/GetOneQueueAverage/" + station;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.print(response.toString());
                average.setText(response.toString());

                RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
                //get queue length
                String url1 = "https://192.168.1.5:44323/api/queue/vehicleQueue/GetOneQueueLength/" + station;
                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.print(response.toString());
                        total.setText(response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                queue2.add(stringRequest1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        loadData();

        queue.add(stringRequest);


        //add new queue detail
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url3 = "https://192.168.1.5:44323/api/queue/vehicleQueue";
                String obj = "{'StationId': '" +
                        station +
                        "', 'customerId': '" +
                        id +
                        "','veihicleModel': '" +
                        vehicleType +
                        "','queueArrivalTime': '" +
                        java.time.LocalDateTime.now() + "' }";

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url3, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            QueueId = response.getString("vehicleQueueId").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        intentToQueueClass(QueueId);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println(volleyError);
                    }
                });
                requestQueue.add(jsonObjectRequest);
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowFuelInfo.class);
                intent.putExtra("station", station);
                intent.putExtra("fuelType", fuel);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });
    }

    private void intentToQueueClass(String qid) {
        Intent intent = new Intent(getApplicationContext(), InsertUserQueue.class);
        System.out.println("fetch qid" + " " + qid);
        intent.putExtra("station", station);
        intent.putExtra("id", id);
        intent.putExtra("queueId", qid);
        startActivity(intent);
    }

    //SSL handshake
    private void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }


    @SuppressLint("Range")
    private void loadData() {
        liteDB = database.getWritableDatabase();
        cursor = liteDB.query(SQLHelper.TABLE_NAME, null, " " + SQLHelper.Table_Column_2_Email + "=?", new String[]{email}, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();
                vehicleType = cursor.getString(cursor.getColumnIndex(SQLHelper.Table_Column_10_VehicleType));
                cursor.close();
            }
        }
    }
}