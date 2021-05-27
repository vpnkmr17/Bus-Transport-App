package com.example.bususerapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bususerapp.Classes.SecurityQuestions;
import com.example.bususerapp.Classes.User;
import com.example.bususerapp.R;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

public class ProfileViewActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private ImageView imageView;
    private TextView textViewUser, textViewSchool;
    private Button buttonCall, buttonMessage;

    private String imageUrl;
    private String imageName;
    private String userId;
    private String phoneNum;

    public static final String POST_USER_ID = "com.example.bususerapp.postuserid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        firebaseAuth = FirebaseAuth.getInstance();

        // If user not login in, return to login activity

        Intent intent = getIntent();

        // Initialize
        imageView = (ImageView) findViewById(R.id.imageView);
        textViewUser = (TextView) findViewById(R.id.textViewUser);
        textViewSchool = (TextView) findViewById(R.id.textViewSchool);
        buttonCall = (Button) findViewById(R.id.buttonCall);
        buttonMessage = (Button) findViewById(R.id.buttonMessage);

        userId = intent.getStringExtra(PostViewActivity.POST_PROFILE);

        // Set listeners
        buttonCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNum, null));
                startActivity(intent);
            }
        });

        buttonMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra(POST_USER_ID,userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Populate user information including image
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("INFO").getValue(User.class);
                if (user != null){
                    textViewUser.setText(user.getName());
                    phoneNum = user.getPhoneNum();
                    textViewSchool.setText(user.getUsername());
                    imageUrl = dataSnapshot.child("IMAGE").child("imageUrl").getValue(String.class);
                    imageName = dataSnapshot.child("IMAGE").child("name").getValue(String.class);
                    Picasso.get().load(imageUrl).resize(300,300).into(imageView);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {

    }
}