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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.bususerapp.MainActivity;
import com.example.bususerapp.R;
import com.example.bususerapp.ScheduleActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayScheduleActivity extends AppCompatActivity {


    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_schedule);

        extras = getIntent().getExtras();
        String s[] = extras.getStringArray("Schedule");

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, s);

        ListView listView = (ListView)findViewById(R.id.parent_layout);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this::ClickBusStop);
    }

    private void ClickBusStop(AdapterView<?> adapterView, View view, int i, long l) {
        //adapterView.getSelectedItem().toString();
        ListView listView = (ListView)findViewById(R.id.parent_layout);
        String query = adapterView.getItemAtPosition(i).toString();
        Log.i("Clicked on bus stop", String.valueOf(adapterView.getItemAtPosition(i)));
        Toast.makeText(this, String.valueOf(adapterView.getItemAtPosition(i)), Toast.LENGTH_SHORT).show();
        AndroidNetworking.post("https://bus-xapi.herokuapp.com/api/busno/")
                .addBodyParameter("bus_no", query)
                .setTag("Bus_no")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    String bus_no = query;
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.i("Size", response.toString());
                        JSONObject li;
                        try {
                            li = response.getJSONObject("Accepted");
                            JSONArray list = li.getJSONArray(bus_no);
                            int len = list.length();
                            System.out.println("Length is" + len);
                            String s[] = new String[len];
                            for(int i=0; i<len; i++)
                            {
                                JSONArray bus = list.getJSONArray(i);
                                String station_name = bus.getString(0);
                                s[i] = station_name;
                                //Log.i("Bus station name", s[i]);
                            }
                            Intent intent = new Intent(DisplayScheduleActivity.this, DisplayBusStopNamesActivity.class);
                            intent.putExtra("BusStopNames", s);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //finish();
                        // Redirect to login activity
                        Log.i("result", "successful");
                        Toast.makeText(DisplayScheduleActivity.this, query, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(DisplayScheduleActivity.this,"bus not found",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}