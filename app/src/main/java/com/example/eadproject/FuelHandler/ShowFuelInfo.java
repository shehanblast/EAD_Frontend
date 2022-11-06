package com.example.eadproject.FuelHandler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eadproject.QueueHandler.ShowQueueTotal;
import com.example.eadproject.R;

/*
 *  Fuel info class
 * */
public class ShowFuelInfo extends AppCompatActivity {

    private String station, fuel, city;
    private TextView textViewStation, textViewFuel, textViewCity, textViewArrivalTime, textViewFinishTime;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fuel_details);

        station = getIntent().getStringExtra("station");
        fuel = getIntent().getStringExtra("fuelType");
        city = getIntent().getStringExtra("city");

        textViewStation = findViewById(R.id.txtViewFuelDetails);
        textViewFuel = findViewById(R.id.txtViewFuelDetailsFuelType);
        textViewArrivalTime = findViewById(R.id.txtViewFuelStationArrivalTime);
        textViewFinishTime = findViewById(R.id.txtViewFuelStationFinishFuel);
        back = findViewById(R.id.btnViewQueue2);

        textViewStation.setText(station);
        textViewFuel.setText(fuel);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowQueueTotal.class);
                intent.putExtra("station",station);
                intent.putExtra("fuelType", fuel);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

    }
}