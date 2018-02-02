package com.example.john.mobicare_uganda.authentications;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.john.mobicare_uganda.MainActivity;
import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.welcome.MainActivity2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import connectivity.Constants;
import connectivity.DBHelper;
import connectivity.SessionManager;
import server_connections.Fetch;

import static connectivity.Constants.config.HOST_URL;
import static connectivity.Constants.config.OPERATION_PATIENTS;
import static connectivity.Constants.config.OPERATION_SMS;
import static connectivity.Constants.config.SLECTED_PATIENT_URL;
import static connectivity.Constants.config.SMS_PATIENT_URL;

/**
 * Created by john on 1/20/18.
 */
public class Password_Verifications extends AppCompatActivity {
    private EditText input_number,input_password;
    private Button btn_signin;
    String number, password,user_type;
    private Context context = this;
    private String registration  ;
    private TextView sign_text;
    private static final String TAG = "Password_Verifications";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_entry);
        input_number = (EditText) findViewById(R.id.input_number);
        input_password = (EditText) findViewById(R.id.input_password);
        sign_text = (TextView) findViewById(R.id.sign_text);


        btn_signin = (Button) findViewById(R.id.button_start_verification);
        number = getIntent().getStringExtra("phone");
        password = getIntent().getStringExtra("password");
        user_type = getIntent().getStringExtra("type");
        if (getIntent().getStringExtra("registration") != null){
            registration = getIntent().getStringExtra("registration");
            if (registration.equals("register")){
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Syncing details....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                if (user_type.equals("user")){
                    selected(number,progressDialog);
                }
            }
        }
        if (number != null){
            input_number.setText(number);
        }
        sign_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,PhoneVerification.class));
            }
        });
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password2 = input_password.getText().toString().trim();
                if (password != null){
                    if (password2.equals(password)){
                        if (user_type.equals("user")){
                            //Intent intent = new Intent(context, Login_Activity.class);
                            //intent.putExtra("phone","256790362890");
                            //startActivity(intent);

                            new SessionManager(context).loginDoctor("");
                            new SessionManager(context).loginUser(number);

                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("welcome","welcome");
                            intent.putExtra("phone",number);
                            startActivity(intent);

                        }else{
                            new SessionManager(context).loginDoctor(number);
                            new SessionManager(context).loginUser("");
                            Intent intent = new Intent(context, Login_Activity.class);
                            intent.putExtra("phone",number);
                            startActivity(intent);
                        }
                        finish();
                        Toast.makeText(context, "Signin Success!...",Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(context, "Signin Failled with wrong password!...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public void selected(final String phone, final ProgressDialog progressDialog){
        final SQLiteDatabase db = DBHelper.getHelper(context).getWritableDatabase();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+SLECTED_PATIENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Result",response);
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                 db.beginTransactionNonExclusive();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(Constants.config.USER_ID, jsonObject.getLong(Constants.config.USER_ID));
                                contentValues.put(Constants.config.FIRST_NAME, jsonObject.getString(Constants.config.FIRST_NAME));
                                contentValues.put(Constants.config.LAST_NAME, jsonObject.getString(Constants.config.LAST_NAME));
                                contentValues.put(Constants.config.GENDER, jsonObject.getString(Constants.config.GENDER));
                                contentValues.put(Constants.config.USER_STATUS, jsonObject.getLong(Constants.config.USER_STATUS));

                                contentValues.put(Constants.config.DISTRICT_ID, jsonObject.getLong(Constants.config.DISTRICT_ID));
                                contentValues.put(Constants.config.REG_DATE, jsonObject.getString(Constants.config.REG_DATE));
                                contentValues.put(Constants.config.CONTACT, jsonObject.getString(Constants.config.CONTACT));

                                contentValues.put(Constants.config.DOB, jsonObject.getString(Constants.config.DOB));
                                contentValues.put(Constants.config.PHONE_SERIAL, jsonObject.getString(Constants.config.PHONE_SERIAL));
                                contentValues.put(Constants.config.USER_PASSWORD, jsonObject.getString(Constants.config.USER_PASSWORD));

                                Log.e(TAG, jsonObject.getString(Constants.config.FIRST_NAME));

                                db.insert(Constants.config.TABLE_PATIENTS, null, contentValues);
                                db.setTransactionSuccessful();
                            }
                            if (progressDialog != null){
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }


                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            try{
                                db.endTransaction();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        Log.e(TAG,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Log.e(TAG, volleyError.getMessage());
                        try {
                            Toast toast = Toast.makeText(context, "Check our internet Connections", Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.setBackgroundResource(R.drawable.round);
                            TextView text = (TextView) view.findViewById(android.R.id.message);
                            //toast.show();
                            //Log.e(TAG, volleyError.getMessage());
                            if (progressDialog != null){
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("phone", phone);
                //Adding parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
