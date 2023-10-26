package com.example.eadproject.OwnerHandler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.eadproject.R;

/*
 *  edit fuel details class
 * */
public class EditFuelInfo extends AppCompatActivity {

    private EditText editName, editStationNo, editArrivalTime, FuelType;
    private Spinner spinnerFinish;
    private Button button, back;
    private RequestQueue requestQueue1;
    private String fuelType, finishStatus;
    private Boolean status;
    private String email, StationId, fuelId, fuelStatus, fuelName, fuelStatus1;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_fuel_details);

        //assign the ids
        FuelType = findViewById(R.id.ddUpdateFuelTypeOwner);
   //     spinnerFinish = findViewById(R.id.ddUpdateFuelFinishStatusOwner);
       // button = findViewById(R.id.btnUpdateFuelDetails);
        back = findViewById(R.id.btnBackUpdateFuelDetails);
        editName = findViewById(R.id.txtUpdateFuelStationOwner);
//        editStationNo = findViewById(R.id.txtUpdateFuelStationNoOwner);
        editArrivalTime = findViewById(R.id.txtUpdateFuelStationArrivalTimeOwner);
        textView = findViewById(R.id.fuelStatusUpdate);

        StationId = getIntent().getStringExtra("StationId");
        fuelId = getIntent().getStringExtra("id");
        fuelStatus = getIntent().getStringExtra("fuelfinish");
        fuelName = getIntent().getStringExtra("fuelName");
        email = getIntent().getStringExtra("email");


        //declare the spinner for fuel
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.fuelType, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerFuelType.setAdapter(adapter);
//
//        //voley library
//        requestQueue1 = Volley.newRequestQueue(getApplicationContext());
//
//        //add fuel details into a spinner
//        spinnerFuelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                fuelType = adapterView.getItemAtPosition(i).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

                //voley library
        requestQueue1 = Volley.newRequestQueue(getApplicationContext());

        //fuel finish details spinner
//        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.finishType, android.R.layout.simple_spinner_item);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerFinish.setAdapter(adapter1);
//
//        //add data to the spinner
//        spinnerFinish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                finishStatus = adapterView.getItemAtPosition(i).toString();
//                System.out.println(finishStatus);
//                if(finishStatus.equals("Yes")){
//                    status = true;
//                }else{
//                    status = false;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        //call handle SSL handshake
        handleSSLHandshake();

        //nav-back
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OwnerPanel.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        editName.setEnabled(false);
//        editStationNo.setEnabled(false);

        editArrivalTime.setEnabled(false);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);
        editArrivalTime.setText(" " + time);

        if ("true".equals(fuelStatus)) {
            fuelStatus1 = "Yes";
        } else {
            fuelStatus1 = "No";
        }
        textView.setText(" " + fuelStatus1);

        FuelType.setText(" " + fuelName);


        //fetch all FuelStation Details
        String url = "https://192.168.1.5:44323/api/fuelStation/FuelStation/FetchStationAccordingtoOwnerId?ownerId="+email;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = response.getJSONObject(0);
                    String obj = object.getString("stationName");
                    editName.setText(" " + obj);


//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        String obj = object.getString("ownerId");
//                        if (email.equals(obj)) {
//                            String name = object.getString("OwnerId");
//                            String stationNo = object.getString("stationNo");
//                            editName.setText(name);
//                            editStationNo.setText(stationNo);
//                        }
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);

        //update fuel details
//        button.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onClick(View view) {
//                System.out.println("inside on click");
//                String url = "https://192.168.1.5:44323/api/fuelInfo/FuelDetails/" + email;
//                String obj = "{'StationId': '" +
//                        editName.getText().toString() +
//                        "', 'FuelName': '" +
//                        fuelType +
//                        "','FuelArrivalTime': '" +
//                        java.time.LocalDateTime.now() +
//                        "','FuelFinish': " +
//                        status + " }";
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(obj);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                JsonObjectRequest jsonObjectRequest =  new JsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        System.out.println(volleyError.toString());
//                    }
//                });
//
//                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//                requestQueue.add(jsonObjectRequest);
//            }
//        });



    }

    //SSL handshake
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
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
