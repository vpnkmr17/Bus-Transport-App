package com.example.bususerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.bususerapp.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewSignin;
    private EditText editTextEmail, editTextUsername, editTextPhone, editTextAddress, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    private String userId;

    private ProgressDialog progressDialog;

    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        AndroidNetworking.initialize(getApplicationContext());

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        progressDialog = new ProgressDialog(this);

        // Set listeners
        textViewSignin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // If email is empty, please enter email
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        // If username is empty, please enter username
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        // If phone is empty, please enter mobile number
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        // If address is empty, please enter address
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            return;
        }

        // If password is empty, please enter password
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // If confirm password is empty, please confirm
        if (TextUtils.isEmpty(confirmPassword) || !password.equals(confirmPassword)) {
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            FirebaseUser currUser = firebaseAuth.getCurrentUser();
                            userId = currUser.getUid();
                            currUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        // Successfully registered user, please verify through user email
                                        Toast.makeText(context, "Please check email for verification.", Toast.LENGTH_SHORT).show();
                                        progressDialog.setMessage("Redirecting");
                                        progressDialog.show();
                                        AndroidNetworking.post("https://bestbus-api.herokuapp.com/api/create/")
                                                .addBodyParameter("email", email)
                                                .addBodyParameter("username", username)
                                                .addBodyParameter("password", password)
                                                .addBodyParameter("phone", phone)
                                                .addBodyParameter("address", address)
                                                .setTag("User Registered")
                                                .setPriority(Priority.MEDIUM)
                                                .build()
                                                .getAsJSONObject(new JSONObjectRequestListener() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        Toast.makeText(RegisterActivity.this, "User has been registered", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        // Redirect to login activity
                                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                    }
                                                    @Override
                                                    public void onError(ANError error) {
                                                        Toast.makeText(RegisterActivity.this,"Could not registered ... Please try again",Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    else{
                                        Toast.makeText(context, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterActivity.this,"Could not registered ... Please try again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        /*AndroidNetworking.post("https://bestbus-api.herokuapp.com/api/create/")
                .addBodyParameter("email", email)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .addBodyParameter("phone", phone)
                .addBodyParameter("address", address)
                .setTag("User Registered")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        finish();
                        // Redirect to login activity
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(RegisterActivity.this,"Could not registered ... Please try again",Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    @Override
    public void onClick(View view) {
        if (view == buttonRegister){
            // Register user
            registerUser();
        }
        else if (view == textViewSignin){
            finish();
            // Redirect to login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}