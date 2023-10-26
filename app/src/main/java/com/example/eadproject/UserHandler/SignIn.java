package com.example.eadproject.UserHandler;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.eadproject.SQLHelper.SQLHelper;
import com.example.eadproject.OwnerHandler.OwnerPanel;
import com.example.eadproject.R;
import com.example.eadproject.UserPanel.Panel;

/*
 *  Login class
 * */
public class SignIn extends AppCompatActivity {

    private TextView textView;
    private EditText editTextemail, editTextpassword;
    private Button button;
    SQLHelper DB;
    private Boolean EditTextEmptyHolder;
    private String email, password,Role;
    private SQLiteDatabase sqLiteDatabaseObj;
    private Cursor cursor;
    private String TempPassword = "NOT_FOUND";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextemail = findViewById(R.id.loginTxtEmail);
        editTextpassword = findViewById(R.id.loginTxtPassword);
        button = findViewById(R.id.btnlogin);
        textView = findViewById(R.id.loginTxtDontHaveAcc);
        DB = new SQLHelper(this);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = editTextemail.getText().toString();
                String pass = editTextpassword.getText().toString();

                CheckEditTextStatus();
                // Calling login method.
                LoginFunction();

                EmptyEditTextAfterDataInsert();
            }
        });
    }

    private void EmptyEditTextAfterDataInsert() {
        editTextemail.getText().clear();
        editTextpassword.getText().clear();
    }

    @SuppressLint("Range")
    private void LoginFunction() {
        if (EditTextEmptyHolder) {
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                sqLiteDatabaseObj = DB.getWritableDatabase();
                cursor = sqLiteDatabaseObj.query(SQLHelper.TABLE_NAME,
                        null,
                        " " + SQLHelper.Table_Column_2_Email + "=?",
                        new String[]{email},
                        null,
                        null,
                        null);
                while (cursor.moveToNext()) {
                    if (cursor.isFirst()) {
                        cursor.moveToFirst();
                        TempPassword = cursor.getString(cursor.getColumnIndex(SQLHelper.Table_Column_13_Password));
                        Role = cursor.getString(cursor.getColumnIndex(SQLHelper.Table_Column_12_RoleType));
                        cursor.close();
                    }
                }
                CheckFinalResult();
                EmptyEditTextAfterDataInsert();
            }
            else{
                Toast.makeText(SignIn.this, "invalid email!", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(SignIn.this, "Enter UserName or Password.", Toast.LENGTH_LONG).show();
        }
    }

    public void CheckEditTextStatus() {
        email = editTextemail.getText().toString();
        password = editTextpassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            EditTextEmptyHolder = false;
        } else {
            EditTextEmptyHolder = true;
        }
    }


    public void CheckFinalResult() {
        if (TempPassword.equalsIgnoreCase(password)) {
            Toast.makeText(SignIn.this, "Successfully logged in", Toast.LENGTH_LONG).show();
            System.out.println(Role);
            if("User".equals(Role)){
                Intent intent = new Intent(SignIn.this, Panel.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }else if("Owner".equals(Role)){
                Intent intent = new Intent(SignIn.this, OwnerPanel.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        } else {
            Toast.makeText(SignIn.this, "UserName or Password made be is Wrong, Please Try Again.", Toast.LENGTH_LONG).show();
        }
        TempPassword = "NOT_FOUND";
    }
}
