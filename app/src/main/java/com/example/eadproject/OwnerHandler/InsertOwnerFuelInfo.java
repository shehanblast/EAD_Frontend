package com.example.eadproject.OwnerHandler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.eadproject.SQLHelper.SQLHelper;
import com.example.eadproject.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eadproject.UserHandler.Registration;

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

/*
 *  This is Insert fuel details class
 * */
public class InsertOwnerFuelInfo extends AppCompatActivity {

    private Spinner spinnerFuelType, spinnerFinish;
    private Cursor cursor;
    private String email, id;
    private SQLiteDatabase sqLiteDatabaseObj;
    private EditText editTextStationName, getEditTextStationNo, arrivalTime;
    private Boolean status;
    SQLHelper DB;
    private RequestQueue requestQueue1;
    private String fuelType, finishStatus, name, stationNo, time, stationId;
    private Button btn1, btn2, btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_queue_details_owner);

        btn2 = findViewById(R.id.btnBackAddFuel);
        btn3 = findViewById(R.id.btnUpdateFuelOwner);
        editTextStationName = findViewById(R.id.txtAddFuelStationOwner);
//        getEditTextStationNo = findViewById(R.id.txtAddFuelStationNoOwner);
        spinnerFuelType = findViewById(R.id.ddFuelTypeOwner);
        email = getIntent().getStringExtra("email");
        id = getIntent().getStringExtra("id");
        System.out.println("email" + " " + email);
        System.out.println("id" + " " + id);
        DB = new SQLHelper(this);
        spinnerFinish = findViewById(R.id.ddFuelFinishStatusOwner);
        btn1 = findViewById(R.id.btnAddFuelDetails);
        arrivalTime = findViewById(R.id.txtAddFuelStationArrivalTimeOwner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.fuelType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuelType.setAdapter(adapter);

        //this is request queue backend volley library
        requestQueue1 = Volley.newRequestQueue(getApplicationContext());

        handleSSLHandshake();

        spinnerFuelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fuelType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.finishType, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFinish.setAdapter(adapter1);

        spinnerFinish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                finishStatus = adapterView.getItemAtPosition(i).toString();
                System.out.println(finishStatus);
                if(finishStatus.equals("Yes")){
                    status = true;
                }else{
                    status = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //get all fuel details
        String url = "https://192.168.1.5:44323/api/fuelStation/FuelStation/FetchStationAccordingtoOwnerId?ownerId=" + email;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                try {
                    JSONObject object = response.getJSONObject(0);

                    String name = object.getString("stationName");
                    String sId = object.getString("stationId");
                    stationId = sId;
                    System.out.println("id-------"+sId);
                    editTextStationName.setText(" " + name);
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

        //add new fuel detail
        btn1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String url3 = "https://192.168.1.5:44323/api/fuelInfo/fuelInfo";
                String obj = "{'StationId': '" +
                        stationId +
                        "', 'type': '" +
                        fuelType +
                        "','arrivalTime': '" +
                        java.time.LocalDateTime.now() +
                        "','status': " +
                        status +
                        " }";
                ;
                System.out.println("lol name" + " " + stationId);
                System.out.println("lol fuelType" + " " + fuelType);

                //check wheather the fuel info already exists in the table
                String url = "https://192.168.1.5:44323/api/fuelInfo/fuelInfo/FetchFuelInfoFromStationAndFuel?sId=" + stationId + "&fName=" + fuelType;
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    boolean queueStatus = false;
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println(response.toString());
                        try {
                            JSONObject object = response.getJSONObject(0);

                            System.out.println(object == null);
                            if(object != null){
                                //if exists update the arrival time
                                String fid = object.getString("fuelInfoId");
                                String url4 = "https://192.168.1.5:44323/api/fuelInfo/fuelInfo/updateArrivalTime/" + fid;
                                String obj  = "{'arrivalTime': '" +
                                        java.time.LocalDateTime.now() +
                                        "','status': " +
                                        status +
                                        " }";

                                JSONObject jsonObject = null;
                                System.out.println("obj" + " " + obj);
                                Toast.makeText(InsertOwnerFuelInfo.this,"Arrival time Updated!", Toast.LENGTH_LONG).show();
                                try {
                                    jsonObject = new JSONObject(obj);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String stationIdnew = editTextStationName.getText().toString();

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url4, jsonObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            stationId = response.getString("stationId").toString();

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
                                requestQueue1.add(jsonObjectRequest);
                                System.out.println("true path patch req" + " " + fid);
                           }

                        } catch (JSONException e) {
                            //if it doesn't exixts add a new record
                            JSONObject jsonObject = null;
                                System.out.println("obj" + " " + obj);
                            Toast.makeText(InsertOwnerFuelInfo.this,"New Fuel Info is Added!", Toast.LENGTH_LONG).show();
                                try {
                                    jsonObject = new JSONObject(obj);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }


                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url3, jsonObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        System.out.println(volleyError.toString());
                                    }
                                });
                                requestQueue1.add(jsonObjectRequest);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println(volleyError.toString());
                    }
                });

                requestQueue1.add(jsonArrayRequest);

            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OwnerPanel.class);
                intent.putExtra("email", email);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewAllFuelDetails.class);
                intent.putExtra("email", email);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        editTextStationName.setEnabled(false);

        arrivalTime.setEnabled(false);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);
        arrivalTime.setText(" " + time);
        //loadData();
    }

    //This is the handle  SSL Handshake Function
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
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void AddingData() {
//        System.out.println("inside on click");
//        String url3 = "https://192.168.202.134:44323/api/fuel/FuelDetails";
//        String obj = "{'StationId': '" + stationId1 + "', 'FuelName': '" + fuelType + "','FuelArrivalTime': '" + java.time.LocalDateTime.now() + "','FuelFinish': " + status + " }";
//
//        System.out.println(obj);
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = new JSONObject(obj);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        String stationIdnew = editTextStationName.getText().toString();
//        checkData(stationIdnew);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url3, jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    stationId = response.getString("stationId").toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Toast.makeText(AddFuelDetailsOwner.this, "Adding Successfully", Toast.LENGTH_SHORT).show();
//                System.out.println(response.toString());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                System.out.println(volleyError.toString());
//            }
//        });
//        requestQueue1.add(jsonObjectRequest);
//    }


    @SuppressLint("Range")
    private void loadData() {
        sqLiteDatabaseObj = DB.getWritableDatabase();
        // Adding search email query to cursor.
        cursor = sqLiteDatabaseObj.query(SQLHelper.TABLE_NAME, null, " " + SQLHelper.Table_Column_2_Email + "=?", new String[]{email}, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();

                String stName = cursor.getString(cursor.getColumnIndex(SQLHelper.Table_Column_8_StationName));
                String stNo = cursor.getString(cursor.getColumnIndex(SQLHelper.Table_Column_9_StationNo));

                editTextStationName.setText(" " + stName);
                getEditTextStationNo.setText(" " + stNo);

                // Closing cursor.
                cursor.close();
            }
        }
    }
}