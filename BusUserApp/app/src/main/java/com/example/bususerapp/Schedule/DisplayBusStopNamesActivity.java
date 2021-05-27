package com.example.bususerapp.Schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bususerapp.MainActivity;
import com.example.bususerapp.R;

public class DisplayBusStopNamesActivity extends AppCompatActivity {

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_bus_stop_names);

        extras = getIntent().getExtras();
        String s[] = extras.getStringArray("BusStopNames");

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, s);

        ListView listView = (ListView)findViewById(R.id.parent_layout);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this::ClickBusStop);
    }

    private void ClickBusStop(AdapterView<?> adapterView, View view, int i, long l) {
        //adapterView.getSelectedItem().toString();
        ListView listView = (ListView)findViewById(R.id.parent_layout);
        Log.i("Clicked on bus stop", String.valueOf(adapterView.getItemAtPosition(i)));
        Toast.makeText(this, String.valueOf(adapterView.getItemAtPosition(i)), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}