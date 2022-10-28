package com.example.eadproject.OwnerHandler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.eadproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewAllFuelDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Fuel> fuels;
    Adapter adapter;
    private String email, id,stationId1;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_fuel_details);

        recyclerView = findViewById(R.id.fuelList);
        fuels = new ArrayList<>();
        btn = findViewById(R.id.btnBackViewAll);

        email = getIntent().getStringExtra("email");
        id = getIntent().getStringExtra("id");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
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
                        String obj = object.getString("ownerId");
                        if (email.equals(obj)) {
                            String stationId = object.getString("stationId");
                            String stationName = object.getString("name");
                            String email = object.getString("ownerId");
                            System.out.println(stationId);
                            LoadRecycler(stationId,stationName,email);
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

    private void LoadRecycler(String stationId, String station, String newemail) {
        System.out.println(stationId);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://192.168.202.134:44323/api/fuel/FuelDetails/GetOneStation/" + stationId;
        JsonArrayRequest jsonArrayRequestStation = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Station res");
                System.out.println(response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);

                        Fuel fuel = new Fuel();
                        fuel.setFuelId(object.getString("fdId").toString());
                        fuel.setStationId(stationId);
                        fuel.setStationName(station);
                        fuel.setEmail(newemail);
                        fuel.setFuelType(object.getString("fuelName").toString());
                        fuel.setFinishStatus((object.getBoolean("fuelFinish")));
                        fuels.add(fuel);
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(ViewAllFuelDetails.this));
                    adapter = new Adapter(ViewAllFuelDetails.this,fuels);
                    recyclerView.setAdapter(adapter);

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
}