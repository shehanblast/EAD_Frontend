package com.example.eadproject.OwnerHandler;

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
import com.example.eadproject.R;
import com.example.eadproject.UserHandler.SignIn;

/*
 *  Owner dashboard class
 * */
public class OwnerPanel extends AppCompatActivity {

    private CardView cardView1,cardView2;
    private SwipeRefreshLayout layout;
    private SQLiteDatabase sqLiteDatabaseObj;
    private Cursor cursor;
    private String email,id;
    private TextView textView;
    private ImageView button;
    SQLHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        email = getIntent().getStringExtra("email");

        cardView1 = findViewById(R.id.ownerCard1);
        cardView2 = findViewById(R.id.card2Owner);
        layout = findViewById(R.id.ownerDashboardRefresh);
        textView =  findViewById(R.id.textViewDashboardOwner);
        button = findViewById(R.id.ownerLogout);

        DB = new SQLHelper(this);

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileOwner.class);
                intent.putExtra("email", email);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InsertOwnerFuelInfo.class);
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
        // Adding search email query to cursor.
        cursor = sqLiteDatabaseObj.query(SQLHelper.TABLE_NAME, null, " " + SQLHelper.Table_Column_2_Email + "=?", new String[]{email}, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();
                // Storing Password associated with entered email.
                String name = cursor.getString(cursor.getColumnIndex(SQLHelper.Table_Column_1_Name));
                id = cursor.getString(cursor.getColumnIndex(SQLHelper.Table_Column_ID));

                textView.setText("Hello!" +" " +name);
                // Closing cursor.
                cursor.close();
            }
        }
    }
}