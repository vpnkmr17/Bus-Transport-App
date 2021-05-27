package com.example.bususerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bususerapp.Activities.LostFoundMainActivity;
import com.example.bususerapp.Activities.PostActivity;
import com.example.bususerapp.Activities.ProfileActivity;
import com.example.bususerapp.Activities.ProfileViewActivity;
import com.example.bususerapp.Classes.Post;
import com.example.bususerapp.Classes.User;
import com.example.bususerapp.Geofence.GeofenceActivity;
import com.example.bususerapp.LiveTrack.DisplayBusActivity;
import com.example.bususerapp.Schedule.ScheduleBusNoActivity;
import com.example.bususerapp.Schedule.ScheduleSDActivity;
import com.example.bususerapp.TicketBooking.TicketActivity;
import com.example.bususerapp.TicketBooking.TicketBookingMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    public static TextView textView;
    Bundle extras;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public static SharedPreferences sharedPreferences;
    public static String key = "1";
    boolean isLoggedIn = false;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        textView = (TextView) findViewById(R.id.logout);
        //Toast.makeText(this, textView.getText(), Toast.LENGTH_SHORT).show();
        extras = getIntent().getExtras();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();

        if (sharedPreferences!= null) {// to avoid the NullPointerException
            isLoggedIn = sharedPreferences.getBoolean(key, false);
            token = sharedPreferences.getString("Token", null);
        }
        Log.i("", String.valueOf(isLoggedIn));
        if(isLoggedIn)
        {
            textView.setText("Logout");
            //sharedPreferences.edit().putBoolean(key, false).commit();
            Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show();
        }

        else{
            textView.setText("Login");
            Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();
        }

    }

    public void ClickMenu(View view)
    {   //open drawer
        openDrawer(drawerLayout);
    }

    public void openDrawer(DrawerLayout drawerLayout) {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);

    }

    public void ClickLogo(View view)
    {
        closeDrawer(drawerLayout);
    }

    public void closeDrawer(DrawerLayout drawerLayout) {
        //close drawer layout
        //check condition
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            //drawer is open
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view){
        recreate();
    }


    public void ClickSchedule(View view)
    {
        redirectActivity(this, ScheduleActivity.class);
    }

    public void ClickLiveLocation(View view){

        if(!isLoggedIn)
        {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        redirectActivity(this, DisplayBusActivity.class);
    }

    public void ClickTicket(View view){
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();

        if(!isLoggedIn)
        {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        sharedPreferences.edit().putString("Token",token).commit();
        redirectActivity(this, TicketBookingMainActivity.class);
    }

    public void ClickProfile(View view){
        redirectActivity(this, ProfileActivity.class);
    }

    public void ClickLostAndFound(View view){

        if(!isLoggedIn)
        {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        /*if(!isProfileComplete())
        {
            Toast.makeText(this, "Please complete the profile", Toast.LENGTH_SHORT).show();
            return;
        }*/

        redirectActivity(this, LostFoundMainActivity.class);
    }

    public boolean isProfileComplete(){
        firebaseAuth = FirebaseAuth.getInstance();
        final String userId = firebaseAuth.getCurrentUser().getUid();
        final String user = firebaseAuth.getCurrentUser().toString();
        final boolean[] isComplete = {false};

        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i("User Key", userId);
                    Log.i("Key", postSnapshot.getKey());
                    if (postSnapshot.getKey().equals(userId)) {
                        isComplete[0] = true;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.i("Is profile completed", String.valueOf(isComplete[0]));
        //Log.i("User is", user);
        return isComplete[0];
    }

    public void ClickLogout(View view){
        if(textView.getText() == "Login") {
            redirectActivity(this, LoginActivity.class);
            //sharedPreferences.edit().putBoolean(key, false).commit();
            //textView.setText("Logout");
        }
        else
            logout(this);
    }

    public static void logout(Activity activity) {
        //Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set tittle
        builder.setTitle("Logout");

        builder.setMessage("Are you sure you want to logout");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
                Intent i = new Intent(activity, MainActivity.class);
                sharedPreferences.edit().putBoolean(key, false).commit();
                //i.putExtra(key, false);
                activity.startActivity(i);
                //sharedPreferences.edit().putBoolean(key, false).commit();
                //System.exit(0);
            }
        });

        //Negative No button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }

    public static void redirectActivity(Activity activity, Class aClass) {
        //Initialize intent
        Intent intent = new Intent(activity, aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        closeDrawer(drawerLayout);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}