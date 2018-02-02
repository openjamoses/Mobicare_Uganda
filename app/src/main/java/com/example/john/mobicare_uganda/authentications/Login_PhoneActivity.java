package com.example.john.mobicare_uganda.authentications;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.mobicare_uganda.MainActivity;
import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.doctors.MainActivity_Doctor;

import connectivity.Constants;
import connectivity.DBHelper;
import connectivity.SessionManager;
import users.CurrentDoctor;
import users.CurrentUser;

/**
 * Created by john on 12/2/17.
 */
public class Login_PhoneActivity extends AppCompatActivity {
    private EditText input_phone,input_password;
    private Button login_btn;
    private TextView forgot_text;
    private Context context = this;
    private String current_phone = "";
    private String user_type = "user";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_logn_activity);

        input_phone = (EditText) findViewById(R.id.editTextEmail);
        input_password = (EditText) findViewById(R.id.editTextPassword);
        forgot_text = (TextView) findViewById(R.id.forgot_text);
        login_btn = (Button) findViewById(R.id.buttonSignin);

        forgot_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,PhoneVerification.class));
            }
        });

        current_phone = new CurrentUser(context).current();
        try {
            if (current_phone.equals("")) {
                current_phone = new CurrentDoctor(context).current();
                user_type = "doctor";
            }
            input_phone.setText(current_phone + "");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = input_password.getText().toString().trim();
                if (user_type.equals("user")){
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("welcome", "welcome");
                    startActivity(intent);
                    finish();
                }else {
                    Cursor cursor = new DBHelper(context).loginDoctor(current_phone,password);
                    if(cursor.moveToFirst()){
                        SessionManager session = new SessionManager(context);
                        //sessionManager.loginDoctor(username);
                        do{
                            int useID = cursor.getInt(cursor.getColumnIndex(Constants.config.WORKER_ID));
                            String fname = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_FNAME));
                            String lname = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_LNAME));
                            String gender = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_GENDER));
                            String reg_number = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_REG_NUMBER));
                            String title = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_TITLE));

                            String pass = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_PASSWORD));
                            String contact = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_CONTACT));
                            int status = cursor.getInt(cursor.getColumnIndex(Constants.config.WORKER_STATUS));
                            ///Creating Session..
                            session.createDoctorSession(useID, fname, lname, contact,reg_number,gender,pass,status,title);
                            session.loginDoctor(current_phone);

                        }while (cursor.moveToNext());

                        Intent intent = new Intent(context,MainActivity_Doctor.class);
                        startActivity(intent);
                        finish();
                    }else {

                        Toast toast = Toast.makeText(context, "Username or Password is incorrect!", Toast.LENGTH_LONG);
                        View views = toast.getView();
                        views.setBackgroundResource(R.drawable.round_null);
                        TextView text = (TextView) view.findViewById(android.R.id.message);
             /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
                        toast.show();
                        //Toast.makeText(context,"Username or Password is incorrect!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            //Intent intent = new Intent(context, Welcome_Activity.class);
            //startActivity(intent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
