package com.example.eadproject.FuelHandler;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.example.eadproject.QueueHandler.ShowQueueTotal;
import com.example.eadproject.R;

/*
 *  Fuel Station class
 * */
public class FuelStation extends AppCompatActivity {

    private EditText time;
    private Spinner stationSpinner,typeSpinner,townSpinner;
    private Button button;
    private String fuelType,city,station,email,id;
    String res = "";
    ArrayList<String> stationArray = new ArrayList<>();
    ArrayAdapter<String> adapterCity, adapterStation;
    ArrayList<String> newList = new ArrayList<>();
    ArrayList<String> townArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_station);

        //get values from text fields
        email = getIntent().getStringExtra("email");
        id = getIntent().getStringExtra("id");

        //add ids
        stationSpinner = findViewById(R.id.ddFuelStationCity);
        button = findViewById(R.id.btnFuelStationNext);
        typeSpinner = findViewById(R.id.ddUserFuelTypeStation);
        townSpinner = findViewById(R.id.ddUserFuelStationCity);

        //declare sprinter for fuel type
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.fuelType, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter2);

        //config city and station adapter
        adapterCity = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, townArray);
        adapterStation = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, stationArray);

        //SSL handshake
        handleSSLHandshake();

        //get all the station details
        String url = "https://192.168.1.5:44323/api/fuelStation/FuelStation";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                res = response.toString();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String obj = object.getString("town");
                        townArray.add(obj);
                        System.out.println(townArray);
                    }

                    Set<String> set = new LinkedHashSet<>();
                    set.addAll(townArray);

                    townArray.clear();
                    townArray.addAll(set);


                    adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    townSpinner.setAdapter(adapterCity);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fuelType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        stationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                station = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //get all the town details
        townSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = adapterView.getItemAtPosition(i).toString();
                stationArray.clear();
                String url1 = "https://192.168.1.5:44323/api/fuelStation/FuelStation/FetchStationAccordingtoCity?city=" + city;
                JsonArrayRequest jsonArrayRequestStation = new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        res = response.toString();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                String obj = object.getString("stationName");
                                stationArray.add(obj);
                            }
                            Set<String> set = new LinkedHashSet<>();
                            set.addAll(stationArray);

                            stationArray.clear();

                            stationArray.addAll(set);
                            adapterStation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            stationSpinner.setAdapter(adapterStation);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println(volleyError);
                    }
                });

                RequestQueue requestQueueStation = Volley.newRequestQueue(getApplicationContext());
                requestQueueStation.add(jsonArrayRequestStation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validityCheck(station,fuelType,city);
            }
        });

    }

    //validation check
    private void validityCheck(String station1, String fuelType1, String city1) {
        System.out.println(fuelType1);
        if(station1.equals("") || station1.equals(null) || fuelType1.equals("") || fuelType1.equals(null) || city1.equals(null) || fuelType1.matches("Choose")){
            Toast.makeText(this, "Please Select the items", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(getApplicationContext(), ShowQueueTotal.class);
            intent.putExtra("city", city1);
            intent.putExtra("email", email);
            intent.putExtra("id", id);
            intent.putExtra("station",station1);
            intent.putExtra("fuelType", fuelType1);
            startActivity(intent);
        }
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