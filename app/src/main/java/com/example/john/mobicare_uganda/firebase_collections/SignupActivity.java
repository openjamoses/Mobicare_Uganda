package com.example.john.mobicare_uganda.firebase_collections;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.dbsyncing.Password;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import users.CurrentDoctor;
import users.CurrentUser;

/**
 * Created by john on 10/18/17.
 */

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private Context context = this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_email1);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            auth.signOut();
        }
        btnSignIn = (Button) findViewById(R.id.already_btn);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("please wait...");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }


                progressDialog.show();
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                try {
                                    Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                    if (progressDialog.isShowing()){
                                        progressDialog.dismiss();
                                    }
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {

                                        FirebaseUser user = auth.getCurrentUser();
                                        if (user != null) {
                                            if (user.isEmailVerified()) {
                                                //Toast.makeText(LoginActivity.this,"You are in =)",Toast.LENGTH_LONG).show();
                                                String email = user.getEmail();
                                                if (email != null) {

                                                    String current_phone = new CurrentUser(context).current();
                                                    if (current_phone.equals("")) {
                                                        current_phone = new CurrentDoctor(context).current();
                                                        new Password(context).send(current_phone, email, password, 1, "save");
                                                    }else {
                                                        new Password(context).send(current_phone, email, password, 1, "save");
                                                    }
                                                   // new User(context).updateUserStatus(email, 1);
                                                }
                                            } else {
                                                user.sendEmailVerification();
                                                String current_phone = new CurrentUser(context).current();
                                                if (current_phone.equals("")) {
                                                    current_phone = new CurrentDoctor(context).current();
                                                    new Password(context).send(current_phone, email, password, 0, "save");
                                                }else {
                                                    new Password(context).send(current_phone, email, password, 0, "save");
                                                }
                                            }
                                        }


                                        finish();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });

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

    @Override
    protected void onResume() {
        super.onResume();
    }
}
