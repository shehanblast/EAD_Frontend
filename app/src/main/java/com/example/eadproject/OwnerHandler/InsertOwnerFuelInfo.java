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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        getEditTextStationNo = findViewById(R.id.txtAddFuelStationNoOwner);
        spinnerFuelType = findViewById(R.id.ddFuelTypeOwner);
        email = getIntent().getStringExtra("email");
        id = getIntent().getStringExtra("id");
        DB = new SQLHelper(this);
        spinnerFinish = findViewById(R.id.ddFuelFinishStatusOwner);
        btn1 = findViewById(R.id.btnAddFuelDetails);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.fuelType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuelType.setAdapter(adapter);

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //get all fuel details
        String url = "https://192.168.1.5:4432/api/fuelStation/FuelStation";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String obj = object.getString("ownerId");
                        if (email.equals(obj)) {
                            String name = object.getString("OwnerId");
                            String stationNo = object.getString("stationNo");
                            editTextStationName.setText(name);
                            getEditTextStationNo.setText(stationNo);
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

        //add new fuel detail
        btn1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String url3 = "https://192.168.1.5:4432/api/fuelInfo/FuelDetails";
                String obj = "{'StationId': '" +
                        editTextStationName.getText().toString() +
                        "', 'FuelName': '" +
                        fuelType + "','FuelArrivalTime': '" +
                        java.time.LocalDateTime.now() +
                        "','FuelFinish': " +
                        status +
                        " }";
                ;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String stationIdnew = editTextStationName.getText().toString();
                checkData(stationIdnew);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url3, jsonObject, new Response.Listener<JSONObject>() {
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
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OwnerPanel.class);
                startActivity(intent);
            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditFuelInfo.class);
                startActivity(intent);
            }
        });

        editTextStationName.setEnabled(false);
        getEditTextStationNo.setEnabled(false);

        loadData();
    }

    //get detail of a single fuel station
    private void checkData(String stationIdNew) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String status;
        String url = "https://192.168.1.5:4432/api/fuelInfo/FuelDetails/GetOneStation/" + stationIdNew;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = response.toString();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

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

                editTextStationName.setText(stName);
                getEditTextStationNo.setText(stNo);

                // Closing cursor.
                cursor.close();
            }
        }
    }
}