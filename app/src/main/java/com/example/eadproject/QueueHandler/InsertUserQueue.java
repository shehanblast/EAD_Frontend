package com.example.eadproject.QueueHandler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eadproject.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*
 *  Add new Queue detail class
 * */
public class InsertUserQueue extends AppCompatActivity {

    private String station,id,queueId;
    private Button btn1,btn2;
    private EditText text1,text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_queue_details);

        text1 = findViewById(R.id.txtAddQueueDataQueueLength);
        text2 = findViewById(R.id.txtAddQueueDataYourTime);
        btn1 = findViewById(R.id.btnAddQueueDataRefresh);
        station = getIntent().getStringExtra("station");
        id = getIntent().getStringExtra("id");
        queueId = getIntent().getStringExtra("queueId");
        btn2 = findViewById(R.id.btnAddQueueDataExit);

        handleSSLHandshake();

        getYourTimeValue();

        getStationLength();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getYourTimeValue();
                getStationLength();
            }
        });

        //Update queue departure time
        btn2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String url = "https://192.168.1.5:44323/api/queue/vehicleQueue/" + queueId;
//                String obj = "{'queueDepartureTime': '" + "2022-10-28T17:19:38.731Z" + "'}";
//                String obj = "{'StationId': '" +
//                        station +
//                        "', 'customerId': '" +
//                        id +
//                        "','veihicleModel': '" +
//                        "car" +
//                        "','queueArrivalTime': '" +
//                        java.time.LocalDateTime.now() +
//                        "','queueDepartureTime': '" +
//                        java.time.LocalDateTime.now() +
//                        "' }";
                String obj = "{ 'queueDepartureTime': '" +
                        java.time.LocalDateTime.now() +
                        "' }";

                System.out.println(java.time.LocalDateTime.now());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest =  new JsonObjectRequest(Request.Method.PATCH, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println(volleyError.toString());
                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

    //get queue details according to user
    private void getYourTimeValue() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://192.168.1.5:44323/api/queue/vehicleQueue/FetchQueueArrivalTime/" + queueId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                text1.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    //get queue length
    private void getStationLength() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url1 = "https://192.168.1.5:44323/api/queue/vehicleQueue/GetOneQueueLength/" + station;
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                text2.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest1);
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
}