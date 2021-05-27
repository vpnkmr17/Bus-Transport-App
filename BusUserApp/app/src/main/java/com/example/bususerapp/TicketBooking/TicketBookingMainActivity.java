package com.example.bususerapp.TicketBooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.bususerapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TicketBookingMainActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView bus_no;
    AutoCompleteTextView dest;
    ArrayAdapter<String> arrayAdapter;
    Button next;
    JSONObject jsonObject;
    SharedPreferences sharedPreferences;
    String token;

    @Override
    public void onResume(){
        super.onResume();
        autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.src);

        String[] source={"Ahilyabai Holkar Chowk","Ankur Hospital","Thane","Airoli","Ankur"};
        arrayAdapter=new ArrayAdapter<>(this,R.layout.dropdown_item,source);
        autoCompleteTextView.setAdapter(arrayAdapter);


        dest=(AutoCompleteTextView)findViewById(R.id.dest);

        String[] destination={"Mantralaya","Thane","Airoli Bus Station","Airoli","Ankur"};
        arrayAdapter=new ArrayAdapter<>(this,R.layout.dropdown_item,destination);
        dest.setAdapter(arrayAdapter);

        bus_no=(AutoCompleteTextView)findViewById(R.id.bus_no);

        String[] bus={"545 LTD","525 LTD","492 LTD"};
        arrayAdapter=new ArrayAdapter<>(this,R.layout.dropdown_item,bus);
        bus_no.setAdapter(arrayAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_booking_main);

        next=(Button)findViewById(R.id.next);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();
        if(sharedPreferences != null)
        {
            token = sharedPreferences.getString("Token", null);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(this, "Source is "+autoCompleteTextView.getText(), Toast.LENGTH_SHORT).show();
                Log.i("info","Source is "+autoCompleteTextView.getText());
                Log.i("info","Destination is "+dest.getText());
                Log.i("info","Bus No is "+bus_no.getText());


                Editable srct=autoCompleteTextView.getText();
                String source=srct.toString().trim();
                Editable destt=dest.getText();
                String destination=destt.toString().trim();
                Editable bust=bus_no.getText();
                String bus_no=bust.toString().trim();
                final int[] amt = {18};

                jsonObject = new JSONObject();
                try {

                    jsonObject.put("bus_no",bus_no);
                    jsonObject.put("src", source);
                    jsonObject.put("dest", destination);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AndroidNetworking.initialize(getApplicationContext());

                AndroidNetworking.post("https://bus-xapi.herokuapp.com/api/payment/")
                        .addJSONObjectBody(jsonObject)
                        .setTag("test")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // do anything with response
                                try {
                                    Log.i("info",response.toString());
                                    amt[0] =response.getInt("amount");
                                    if (amt[0]==0){
                                        Toast.makeText(TicketBookingMainActivity.this, "Please Enter Valid Ticket Details!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Log.d("info",response.toString(4));
                                    Log.i("info",String.valueOf(amt[0]));
                                    sharedPreferences.edit().putString("Token",token).commit();
                                    Intent send = new Intent(TicketBookingMainActivity.this, TicketActivity.class);
                                    send.putExtra("source",source);
                                    send.putExtra("amount",String.valueOf(amt[0]));
                                    send.putExtra("destination",destination);
                                    send.putExtra("bus_no",bus_no);
                                    startActivity(send);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.i("info","kuch output aaya kya yaha..?");
                            }
                            @Override
                            public void onError(ANError error) {
                                // handle error
                                Log.i("info","there is some error.");
                                Log.i("info",error.toString());

                            }
                        });

            }
        });

    }
}