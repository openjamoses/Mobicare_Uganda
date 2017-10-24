package com.example.john.mobicare_uganda.firebase_collections;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.mobicare_uganda.MainActivity;
import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.dbsyncing.Password;
import com.example.john.mobicare_uganda.doctors.Login_Activity;
import com.example.john.mobicare_uganda.doctors.MainActivity_Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import connectivity.Constants;
import connectivity.DBHelper;
import connectivity.SessionManager;
import users.CurrentDoctor;
import users.CurrentUser;

/**
 * Created by john on 10/18/17.
 */

public class Login extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private Button  btnLogin;
    private Context context = this;
    private TextView textViewSignUp,forgot_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_logn_activity);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            auth.signOut();
            //startActivity(new Intent(context, MainActivity.class));
            //finish();
        }

        inputEmail = (EditText) findViewById(R.id.editTextEmail);
        inputPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        forgot_text = (TextView) findViewById(R.id.forgot_text);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("please wait...");
        btnLogin = (Button) findViewById(R.id.buttonSignin);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        try{
            final FirebaseUser user = auth.getCurrentUser();
            if (user != null){
                String email = user.getEmail();
                auth.signOut();
            }

            String current_phone = new CurrentUser(context).current();
            if (current_phone.equals("")) {
                current_phone = new CurrentDoctor(context).current();
            }
            String p_n = new Password(context).selectPhone(current_phone);
            inputEmail.setText(p_n);
        }catch (Exception e){
            e.printStackTrace();
        }

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SignupActivity.class));

            }
        });

        forgot_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();

                try {
                    //authenticate user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        if (password.length() < 6) {
                                            inputPassword.setError(getString(R.string.minimum_password));
                                        } else {
                                            Toast.makeText(Login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                        }
                                    } else {

                                        FirebaseUser user = auth.getCurrentUser();
                                        String current_phone = new CurrentUser(context).current();
                                        if (current_phone.equals("")) {
                                            current_phone = new CurrentDoctor(context).current();
                                            if (user.isEmailVerified()) {
                                                //Toast.makeText(LoginActivity.this,"You are in =)",Toast.LENGTH_LONG).show();
                                                String email = user.getEmail();
                                                if (email != null) {

                                                    new Password(context).send(current_phone, email, password, 1, "update");

                                                    current_phone = new CurrentDoctor(context).current();

                                                    //// TODO: 10/22/17

                                                    Cursor cursor = new DBHelper(context).login_login(email,password);
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
                                                        new Password(context).send(current_phone, email, password, 1, "save");
                                                        Toast.makeText(context,"Unknown Error Occured",Toast.LENGTH_LONG).show();
                                                    }

                                                    //Intent intent = new Intent(context, Login_Activity.class);
                                                    //intent.putExtra("phone", current_phone);
                                                    //ntent.putExtra("user_type", "doctor");
                                                    //startActivity(intent);
                                                    //finish();

                                                }
                                            } else {
                                                user.sendEmailVerification();
                                                new Password(context).send(current_phone, email, password, 0, "update");
                                                Toast toast = Toast.makeText(context, "Email Not varified!", Toast.LENGTH_LONG);
                                                View view = toast.getView();
                                                view.setBackgroundResource(R.drawable.rounded_blank);
                                                TextView text = (TextView) view.findViewById(android.R.id.message);
                        /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
                                                toast.show();
                                            }


                                        } else {
                                            if (user.isEmailVerified()) {
                                                //Toast.makeText(LoginActivity.this,"You are in =)",Toast.LENGTH_LONG).show();
                                                String email = user.getEmail();
                                                if (email != null) {
                                                    new Password(context).send(current_phone, email, password, 1, "update");
                                                    //new SessionManager(context).loginUser(phone);
                                                    Intent intent = new Intent(context, MainActivity.class);
                                                    intent.putExtra("welcome", "welcome");
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            } else {
                                                user.sendEmailVerification();
                                                new Password(context).send(current_phone, email, password, 0, "update");
                                                Toast toast = Toast.makeText(context, "Email Not varified!", Toast.LENGTH_LONG);
                                                View view = toast.getView();
                                                view.setBackgroundResource(R.drawable.rounded_blank);
                                                TextView text = (TextView) view.findViewById(android.R.id.message);
                        /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
                                                toast.show();
                                            }
                                        }
                                    }
                                }
                            });
                }catch (Exception e){
                    e.printStackTrace();

                    String message = new Password(context).login(email,password);
                    if (message.equals("Success")) {
                        String current_phone = new CurrentUser(context).current();
                        if (current_phone.equals("")) {
                            //// TODO: 10/22/17   | Make
                            current_phone = new CurrentDoctor(context).current();

                            Cursor cursor = new DBHelper(context).login_login(email,password);
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
                                Toast.makeText(context,"Unknown Error Occured",Toast.LENGTH_LONG).show();
                            }


                            //Intent intent = new Intent(context, Login_Activity.class);
                            //intent.putExtra("phone", current_phone);
                            //intent.putExtra("user_type", "doctor");
                            //startActivity(intent);
                            //finish();

                        } else {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("welcome", "welcome");
                            startActivity(intent);
                            finish();
                        }
                    }else {

                        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                        View view = toast.getView();
                        view.setBackgroundResource(R.drawable.rounded_blank);
                        TextView text = (TextView) view.findViewById(android.R.id.message);
                        /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
                        toast.show();
                    }
                }
            }
        });
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
