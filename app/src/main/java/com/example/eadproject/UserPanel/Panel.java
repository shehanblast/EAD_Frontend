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
import com.example.eadproject.SQLHelper.SQLHelper;
import com.example.eadproject.QueueHandler.ShowAllQueueUserInfo;
import com.example.eadproject.R;
import com.example.eadproject.FuelHandler.FuelStation;
import com.example.eadproject.FuelHandler.ShowFuelStationInfo;
import com.example.eadproject.UserHandler.SignIn;
import com.example.eadproject.UserHandler.Profile;

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

        //call dashboard buttons
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Panel.this, ShowAllQueueUserInfo.class);
                // Sending Email to Dashboard Activity using intent.
                intent.putExtra("email", email);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Panel.this, ShowFuelStationInfo.class);
                // Sending Email to Dashboard Activity using intent.
//                intent.putExtra("email", email);
//                intent.putExtra("id", id);
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
}