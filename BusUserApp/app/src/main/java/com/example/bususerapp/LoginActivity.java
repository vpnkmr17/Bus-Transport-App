package com.example.bususerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public String isLoggedIn = "false";
    private TextView textViewSignup, textViewResetPassword;
    private EditText editTextEmail, editTextPassword;
    private Button buttonSignin;
    private ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    String token;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        AndroidNetworking.initialize(getApplicationContext());


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        textViewSignup = (TextView) findViewById(R.id.textViewSignup);
        textViewResetPassword = (TextView) findViewById(R.id.textViewResetPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignin = (Button) findViewById(R.id.buttonSignin);

        progressDialog = new ProgressDialog(this);

        // Set Listeners
        textViewSignup.setOnClickListener(this);
        textViewResetPassword.setOnClickListener(this);
        buttonSignin.setOnClickListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // If email is empty, return
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        // If email is empty, return
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Logging Please Wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            // If email is not verified, verify
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (!user.isEmailVerified()){
                                Toast.makeText(LoginActivity.this, "Please Verify email.",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                // start main activity
                                progressDialog.setMessage("Logging Please Wait...");
                                progressDialog.show();
                                AndroidNetworking.post("https://bestbus-api.herokuapp.com/api/login/")
                                        .addBodyParameter("email", email)
                                        .addBodyParameter("password", password)
                                        .setTag("User Logged in")
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                JSONObject jsonObject = null;
                                                try {
                                                    jsonObject = response.getJSONObject("token");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                String token = null;
                                                try {
                                                    token = jsonObject.getString("token");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                Log.i("User registered:", token);
                                                finish();
                                                // Redirect to login activity
                                                String key = "1";
                                                Intent i = new Intent(getBaseContext(), MainActivity.class);
                                                sharedPreferences.edit().putBoolean(key, true).commit();
                                                sharedPreferences.edit().putString("Token", token).commit();
                                                //i.putExtra(key, true);
                                                startActivity(i);
                                                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            }
                                            @Override
                                            public void onError(ANError error) {
                                                Toast.makeText(LoginActivity.this,"Authentication error....",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                        else {
                            // Failed to log in
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        /*AndroidNetworking.post("https://bestbus-api.herokuapp.com/api/login/")
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .setTag("User Logged in")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        finish();
                        // Redirect to login activity
                        String key = "1";
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        sharedPreferences.edit().putBoolean(key, true).commit();
                        //i.putExtra(key, true);
                        startActivity(i);
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(LoginActivity.this,"Authentication error....",Toast.LENGTH_SHORT).show();
                    }
                });*/
    }


    @Override
    public void onClick(View view) {
        if (view == buttonSignin){
            userLogin();
        }
        else if (view == textViewSignup){
            finish();
            startActivity(new Intent(this, RegisterActivity.class ));
        }
    }
}