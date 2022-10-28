package com.example.eadproject.FuelHandler;

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
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eadproject.R;
import com.example.eadproject.UserPanel.Panel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

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
import com.android.volley.toolbox.Volley;

/*
 *  Fuel details show class
 * */
public class ShowFuelStationInfo extends AppCompatActivity {

    private Spinner spinnerFuelStation,spinnerFuelType,spinnerFuelCity;
    private String fuelType, city, station,email,id;
    private Button backButton;
    ArrayList<String> spinnerArray = new ArrayList<>();
    ArrayList<String> spinnerArray2 = new ArrayList<>();
    ArrayAdapter<String> adapter1, adapter3;
    private TextView stationTextViewNo,arrivalTextViewTime,fuelFinishTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fuel_station_details);

        spinnerFuelStation = findViewById(R.id.ddViewUserFuelStation);
        spinnerFuelType = findViewById(R.id.ddViewUserFuelType);
        //can't find in the layout
        spinnerFuelCity = findViewById(R.id.spinnerViewUserFuelCity);
        backButton= findViewById(R.id.btnbackViewUserFuelDetails);
        stationTextViewNo= findViewById(R.id.txtViewFuelStationNo);
        arrivalTextViewTime= findViewById(R.id.txtViewFuelStationArrivalTime);
        fuelFinishTextView= findViewById(R.id.txtViewFuelStationFinishFuel);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.fuelType, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuelType.setAdapter(adapter2);

        spinnerFuelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fuelType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //this is the city type spinner adapter
        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        //this is the station type spinner adapter
        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray2);

        handleSSLHandshake();

        System.out.println("inside on click");
        String url = "https://192.168.202.134:44323/api/station/FuelStation";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("inside res");
                System.out.println(response.toString());

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String obj = object.getString("city");
                        spinnerArray.add(obj);
                        //System.out.println(spinnerArray);
                    }
                    // convert the arraylist into a set
                    Set<String> set = new LinkedHashSet<>();
                    set.addAll(spinnerArray);

                    // delete al elements of arraylist
                    spinnerArray.clear();

                    // add element from set to arraylist
                    spinnerArray.addAll(set);
                    //newList = removeDuplicates(spinnerArray);

                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFuelCity.setAdapter(adapter1);

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

        spinnerFuelCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = adapterView.getItemAtPosition(i).toString();
                spinnerArray2.clear();
                System.out.println(city);
                System.out.println("inside on click");
                String url1 = "https://192.168.202.134:44323/api/station/FuelStation/GetStationCity?city=" + city;
                JsonArrayRequest jsonArrayRequestStation = new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("Station res");
                        //System.out.println(response.toString());

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                String obj = object.getString("name");
                                spinnerArray2.add(obj);
                                System.out.println(spinnerArray2);
                            }
                            Set<String> set = new LinkedHashSet<>();
                            set.addAll(spinnerArray2);

                            // delete al elements of arraylist
                            spinnerArray2.clear();

                            // add element from set to arraylist
                            spinnerArray2.addAll(set);
                            //newList = removeDuplicates(spinnerArray);
                            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerFuelStation.setAdapter(adapter3);

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

                RequestQueue requestQueueStation = Volley.newRequestQueue(getApplicationContext());
                requestQueueStation.add(jsonArrayRequestStation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Panel.class);
                startActivity(intent);
            }
        });

        spinnerFuelStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                station = adapterView.getItemAtPosition(i).toString();
                checkArriavalTime(station,fuelType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void checkArriavalTime(String station, String fuel) {
        System.out.println("inside on click");
        String url = "https://192.168.202.134:44323/api/station/FuelStation";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("inside res");
                System.out.println(response.toString());
//                res = response.toString();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String obj = object.getString("name");
                        if (station.equals(obj)) {
                            String stationId = object.getString("stationId");
                            String stationNo = object.getString("stationNo");

                            LoadFuelDetails(stationId,stationNo,fuel);
                        }
                    }
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
    }

    private void LoadFuelDetails(String stationId, String stationNo, String fuel) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://192.168.202.134:44323/api/fuel/FuelDetails/GetOneStation/" + stationId;
        JsonArrayRequest jsonArrayRequestStation = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Station res");
                System.out.println(response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String obj = object.getString("fuelName");
                        System.out.println(fuel);
                        if (fuel.equals(obj)) {
                            System.out.println(response.toString());
                            String arrivalTime = object.getString("fuelArrivalTime");
                            Boolean fuelFinish = object.getBoolean("fuelFinish");

                            String fuelStatus1 ="";

                            if (fuelFinish == true) {
                                fuelStatus1 = "Yes";
                            } else {
                                fuelStatus1 = "No";
                            }
//                            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
//                            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH);
//                            LocalDate date = LocalDate.parse(arrivalTime, inputFormatter);
//                            String formattedTime = outputFormatter.format(date);
//                            System.out.println(formattedTime);

                            stationTextViewNo.setText(stationNo);
                            //arrivalTextViewTime.setText(formattedTime);
                            fuelFinishTextView.setText(fuelStatus1);

                        }
                    }

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

        queue.add(jsonArrayRequestStation);
    }

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