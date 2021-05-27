package com.example.bususerapp.Schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.bususerapp.MainActivity;
import com.example.bususerapp.R;
import com.example.bususerapp.RegisterActivity;
import com.example.bususerapp.ScheduleActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleSDActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editTextSource, editTextDestination;
    private Button search;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_sdactivity);

        editTextSource = (EditText)findViewById(R.id.source);
        editTextDestination = (EditText)findViewById(R.id.destination);
        search = (Button) findViewById(R.id.search);

        search.setOnClickListener(this);
    }

    private void Search()
    {
        String source = editTextSource.getText().toString().trim();
        String destination = editTextDestination.getText().toString().trim();

        AndroidNetworking.post("https://bus-xapi.herokuapp.com/api/search/")
                .addBodyParameter("source", source)
                .addBodyParameter("destination", destination)
                .setTag("Bus_no")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.i("Size", response.toString());
                        JSONObject list;
                        try {
                            list = response.getJSONObject("Bus");
                            Log.i("Demo is", list.names().toString());
                            int len = list.length();
                            System.out.println("Length is" + len);
                            String s[] = new String[len];
                            for(int i=0; i<len; i++)
                            {

                                s[i] = list.names().getString(i);
                                Log.i("Bus station name", s[i]);
                            }

                            Intent intent = new Intent(ScheduleSDActivity.this, DisplayScheduleActivity.class);
                            intent.putExtra("Schedule", s);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //finish();
                        // Redirect to login activity
                        Log.i("result", "successful");
                        Toast.makeText(ScheduleSDActivity.this, source, Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(ScheduleSDActivity.this,"bus not found",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void ClickBusStop(AdapterView<?> adapterView, View view, int i, long l) {
        //adapterView.getSelectedItem().toString();
        ListView listView = (ListView)findViewById(R.id.parent_layout);
        Log.i("Clicked on bus stop", String.valueOf(adapterView.getItemAtPosition(i)));
        Toast.makeText(this, String.valueOf(adapterView.getItemAtPosition(i)), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view == search){
            Search();
        }
    }
}