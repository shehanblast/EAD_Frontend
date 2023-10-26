package com.example.eadproject.UserHandler;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import com.example.eadproject.SQLHelper.SQLHelper;
import com.example.eadproject.R;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*
 *  Register new user class
 * */
public class Registration extends AppCompatActivity {

    private TextView textView,sigin;
    private EditText editTextName,editTextEmail,editTextMobile,editTextVehicleNo1,editTextVehicleNo2,
            editTextAddress,editTextStationName,editTextStationNo,editTextPassword,editTextRetypepassowrd;
    private String name,email,mobile,vehicleNo1,vehicleNo2,city,address,stationName,stationNo,vehicleTypeAdd,fuelTypeAdd,password,rePassword,role;
    private LinearLayout layout1, layout2;
    private Spinner spinnerRole,spinnerVehicelType,spinnerFuelType,editTextCity;
    private Button button;
    private String roleType,fuelType,vehicleType,cityType;
    String SQLiteDataBaseQueryHolder ;
    Boolean EditTextEmptyHolder;
    String F_Result = "Not_Found";
    Cursor cursor;
    SQLiteDatabase sqLiteDatabaseObj;
    SQLHelper DB;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextVehicleNo1 = findViewById(R.id.txtRegisterVehicleNo1);
        editTextVehicleNo2 = findViewById(R.id.txtRegisterVehicleNo2);
        spinnerFuelType = findViewById(R.id.ddRegisterFuelType);
        layout1 = findViewById(R.id.customerLayer);
        layout2 = findViewById(R.id.ownerLayer);
        button = findViewById(R.id.btnRegisterCreateAccount);
        sigin = findViewById(R.id.txtRegisterSingIn);
        editTextName = findViewById(R.id.txtRegisterFullName);
        editTextEmail = findViewById(R.id.txtRegisterEmail);
        editTextMobile = findViewById(R.id.txtRegisterMobile);
        editTextCity = findViewById(R.id.ddRegisterCity);
        editTextAddress = findViewById(R.id.txtRegisterAddress);
        editTextStationName = findViewById(R.id.txtRegisterStationName);
        editTextStationNo = findViewById(R.id.txtRegisterStationNo);
        editTextPassword = findViewById(R.id.txtRegisterPassword);
        editTextRetypepassowrd = findViewById(R.id.txtRegisterRetypePassword);
        spinnerRole = findViewById(R.id.ddRegisterRole);
        spinnerVehicelType = findViewById(R.id.ddRegisterVehicleType);

        DB = new SQLHelper(this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        handleSSLHandshake();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.role, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                roleType = adapterView.getItemAtPosition(i).toString();

                if( roleType.equals("User")){
                    layout2.setVisibility(View.GONE);
                    layout1.setVisibility(View.VISIBLE);
                    editTextVehicleNo1.setText(null);
                    editTextVehicleNo2.setText(null);
                    editTextAddress.setText(null);
                    editTextStationName.setText(null);
                    editTextStationNo.setText(null);
                }else if(roleType.equals("Owner")){
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);
                    editTextVehicleNo1.setText(null);
                    editTextVehicleNo2.setText(null);
                    editTextAddress.setText(null);
                    editTextStationName.setText(null);
                    editTextStationNo.setText(null);
                }else if(roleType.equals("Choose")){
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    editTextVehicleNo1.setText(null);
                    editTextVehicleNo2.setText(null);
                    editTextAddress.setText(null);
                    editTextStationName.setText(null);
                    editTextPassword.setText(null);
                    editTextStationNo.setText(null);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.vehicleType, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicelType.setAdapter(adapter1);

        spinnerVehicelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                vehicleType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        editTextCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cityType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.cityType, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTextCity.setAdapter(adapter4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDataBaseBuild();
                SQLiteTableBuild();
                CheckEditTextStatus();
                CheckingEmailAlreadyExistsOrNot();
                EmptyEditTextAfterDataInsert();
            }
        });
    }

    public void SQLiteDataBaseBuild(){
        sqLiteDatabaseObj = openOrCreateDatabase(SQLHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    public void SQLiteTableBuild() {
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS "
                + SQLHelper.TABLE_NAME
                + "(" + SQLHelper.Table_Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SQLHelper.Table_Column_1_Name + " VARCHAR, "
                + SQLHelper.Table_Column_2_Email + " VARCHAR, "
                + SQLHelper.Table_Column_3_Mobile + " VARCHAR, "
                + SQLHelper.Table_Column_4_VehicleNo1 + " VARCHAR, "
                + SQLHelper.Table_Column_5_VehicleNo2 + " VARCHAR, "
                + SQLHelper.Table_Column_6_CIty + " VARCHAR, "
                + SQLHelper.Table_Column_7_Address + " VARCHAR, "
                + SQLHelper.Table_Column_8_StationName + " VARCHAR, "
                + SQLHelper.Table_Column_9_StationNo + " VARCHAR, "
                + SQLHelper.Table_Column_10_VehicleType + " VARCHAR, "
                + SQLHelper.Table_Column_11_FuelType + " VARCHAR, "
                + SQLHelper.Table_Column_12_RoleType + " VARCHAR, "
                + SQLHelper.Table_Column_13_Password + " VARCHAR);");
    }

    public void InsertDataIntoSQLiteDatabase(){
        if(EditTextEmptyHolder == true)
        {
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                SQLiteDataBaseQueryHolder = "INSERT INTO " + SQLHelper.TABLE_NAME +" (name,email,mobileNo,vehicleNo1,vehicleNo2,city,address,stationName,stationNo,vehicleType,fuelType,role,password) VALUES('" +name +"', '" +email +"', '" +mobile +"', '" +vehicleNo1 +"', '" +vehicleNo2 +"', '" +city +"', '" +address +"', '" +stationName +"', '" +stationNo +"', '" +vehicleType +"', '" +fuelType +"', '" +role +"', '" +password +"');";
                sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
                sqLiteDatabaseObj.close();
                Toast.makeText(Registration.this,"Successfully Registered!", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(Registration.this,"Invalid email!", Toast.LENGTH_LONG).show();
            }

        }
        else {
            Toast.makeText(Registration.this,"Required Fields cannot be empty!", Toast.LENGTH_LONG).show();
        }
    }
    public void EmptyEditTextAfterDataInsert(){
        editTextMobile.getText().clear();
        editTextStationNo.getText().clear();
        editTextName.getText().clear();
        editTextEmail.getText().clear();
        editTextPassword.getText().clear();
        editTextVehicleNo1.getText().clear();
        editTextVehicleNo2.getText().clear();
        editTextAddress.getText().clear();
        editTextStationName.getText().clear();
    }
    public void CheckEditTextStatus(){
         name = editTextName.getText().toString();
         role = roleType;
         mobile = editTextMobile.getText().toString();
         vehicleNo1 = editTextVehicleNo1.getText().toString();
         email = editTextEmail.getText().toString();
         stationName = editTextStationName.getText().toString();
         stationNo = editTextStationNo.getText().toString();
         vehicleTypeAdd = vehicleType;
         fuelTypeAdd = fuelType;
         password = editTextPassword.getText().toString();
         rePassword = editTextRetypepassowrd.getText().toString();
         vehicleNo2 = editTextVehicleNo2.getText().toString();
         city = cityType;
         address = editTextAddress.getText().toString();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            EditTextEmptyHolder = false ;
        }
        else {
            EditTextEmptyHolder = true ;
        }
    }

    public void CheckingEmailAlreadyExistsOrNot(){
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
                F_Result = "Email Found";
                cursor.close();
            }
        }
        CheckFinalResult();
    }

    public void CheckFinalResult(){
        if("Email Found".equalsIgnoreCase(F_Result))
        {
            Toast.makeText(Registration.this,"Email Exists",Toast.LENGTH_LONG).show();
        }
        else {
            InsertDataIntoSQLiteDatabase();
            RegisterMongodb();
        }
        F_Result = "Not_Found" ;
    }

    private void RegisterMongodb() {
        System.out.println("inside on click");
        String url = "https://192.168.1.5:44323/api/fuelStation/FuelStation";
        String obj = "{'stationName': '" +
                stationName +
                "', 'location': '" +
                address +
                "','ownerId': '" +
                email +
                "','stationNo': '" +
                stationNo +
                "' ,'town': '" +
                cityType +
                "'}";

        System.out.println(obj);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError);
            }
        });
        requestQueue.add(jsonObjectRequest);
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
