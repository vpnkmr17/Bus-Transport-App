package com.example.bususerapp.LiveTrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.bususerapp.MainActivity;
import com.example.bususerapp.R;
import com.example.bususerapp.ScheduleActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DisplayBusActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_bus);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:954049732377:android:8c4d0b8d0e646d3c626f88")
                .setApiKey("AIzaSyDOCqnfP7KIDoO8_jILC8H7dAbfglOarXw")
                .setDatabaseUrl("https://busdriverapp-258fb-default-rtdb.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(this, options, "busdriverapp-258fb");
        FirebaseApp secondApp = FirebaseApp.getInstance("busdriverapp-258fb");

        //FirebaseDatabase firstDatabase = FirebaseDatabase.getInstance();  //default db from JSON
        FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
        Log.i("Keyyy",secondDatabase.getReference("Location").getKey());
        databaseReference = secondDatabase.getReference("Location");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            Integer i=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s[] = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    String demo = postSnapshot.getKey().toString() + " " + postSnapshot.child("bus_no").getValue().toString();
                    s[i] = demo;
                    Log.i("Usersss",s[i]);
                    i++;
                }
                ArrayAdapter adapter = new ArrayAdapter<String>(DisplayBusActivity.this, R.layout.activity_listview, s);

                ListView listView = (ListView)findViewById(R.id.parent_layout);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(DisplayBusActivity.this::ClickBusStop);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ClickBusStop(AdapterView<?> adapterView, View view, int i, long l) {
        //adapterView.getSelectedItem().toString();
        ListView listView = (ListView)findViewById(R.id.parent_layout);
        String[] s;
        s = String.valueOf(adapterView.getItemAtPosition(i)).split(" ");
        Log.i("Clicked on bus stop", String.valueOf(adapterView.getItemAtPosition(i)));
        //for(int j=0;j<s.length;j++)
            //Log.i("String" + String.valueOf(j), s[j]);
        //Log.i("String", s[0]);
        //Log.i("String", s[1]);
        Toast.makeText(this, String.valueOf(adapterView.getItemAtPosition(i)), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LiveMapsActivity.class);
        sharedPreferences.edit().putString("username", s[0]).commit();
        startActivity(intent);
    }
}