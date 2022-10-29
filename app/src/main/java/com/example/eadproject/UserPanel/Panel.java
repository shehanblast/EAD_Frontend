package com.example.eadproject.UserPanel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eadproject.QueueHandler.NoDisplayQueue;
import com.example.eadproject.SQLHelper.SQLHelper;
import com.example.eadproject.QueueHandler.ShowAllQueueUserInfo;
import com.example.eadproject.R;
import com.example.eadproject.FuelHandler.FuelStation;
import com.example.eadproject.FuelHandler.ShowFuelStationInfo;
import com.example.eadproject.UserHandler.SignIn;
import com.example.eadproject.UserHandler.Profile;

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
 *  User dashboard class
 * */
public class Panel extends AppCompatActivity {

    private SwipeRefreshLayout layout;
    SQLHelper DB;
    private TextView textViewMame;
    private ImageView button;
    private CardView card1,card2,card3,card4;
    private SQLiteDatabase sqLiteDatabaseObj;
    private Cursor cursor;
    private String email,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        email = getIntent().getStringExtra("email");

        card1 = findViewById(R.id.card1);
        textViewMame = findViewById(R.id.textViewDashboardUser);
        layout = findViewById(R.id.dashboardRefresh);
        button =  findViewById(R.id.userLogout);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card2 = findViewById(R.id.card2);
        DB = new SQLHelper(this);

        handleSSLHandshake();

        //call dashboard buttons
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Panel.this, ShowAllQueueUserInfo.class);
//                // Sending Email to Dashboard Activity using intent.
//                intent.putExtra("email", email);
//                intent.putExtra("id", id);
//                startActivity(intent);
                loadAllQueueDetails(id);
            }
        });
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Panel.this, ShowFuelStationInfo.class);
                // Sending Email to Dashboard Activity using intent.
                intent.putExtra("email", email);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Panel.this, Profile.class);
                // Sending Email to Dashboard Activity using intent.
                intent.putExtra("email", email);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Panel.this, FuelStation.class);
                // Sending Email to Dashboard Activity using intent.
                intent.putExtra("email", email);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                layout.setRefreshing(false);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });
        loadData();
    }

    private void loadAllQueueDetails(String userId) {
        System.out.println(userId);
        RequestQueue queue1 = Volley.newRequestQueue(Panel.this);
        String url2 = "https://192.168.1.5:44323/api/queue/vehicleQueue/CheckDepartureTime?id=" + userId;
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response.toString());
                    String myObjAsString = jsonResponse.getString("vehicleQueueId");
                    String stationId = jsonResponse.getString("stationId");
                    String arrivalTime = jsonResponse.getString("queueDepartureTime");
                    System.out.println("Plan - loadAllQueueDetails" + " " + stationId);

                    if (myObjAsString != "null") {
                        Intent intent = new Intent(getApplicationContext(), ShowAllQueueUserInfo.class);
                        intent.putExtra("queueId", myObjAsString);
                        intent.putExtra("stationId", stationId);
                        intent.putExtra("arrivalTime", arrivalTime);
                        intent.putExtra("email", email);
                        intent.putExtra("id", Panel.this.id);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NoDisplayQueue.class);
                        intent.putExtra("email", email);
                        intent.putExtra("id", Panel.this.id);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        //add queue string request
        queue1.add(stringRequest2);
    }


    @SuppressLint("Range")
    private void loadData() {
        sqLiteDatabaseObj = DB.getWritableDatabase();
        cursor = sqLiteDatabaseObj.query(SQLHelper.TABLE_NAME, null, " " + SQLHelper.Table_Column_2_Email + "=?", new String[]{email}, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();
                String name = cursor.getString(cursor.getColumnIndex(SQLHelper.Table_Column_1_Name));
                id = cursor.getString(cursor.getColumnIndex(SQLHelper.Table_Column_ID));
                textViewMame.setText("Hello!" +" " +name);
                cursor.close();
            }
        }
    }

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