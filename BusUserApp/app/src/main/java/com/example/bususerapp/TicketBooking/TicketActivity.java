package com.example.bususerapp.TicketBooking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bususerapp.R;

public class TicketActivity extends AppCompatActivity {

    private Button buttonPay;
    private EditText editTextAmount;

    //Payment Amount
    private String paymentAmount;
    String src;
    String dest;
    String bus_no, token;
    SharedPreferences sharedPreferences;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        setTitle("Payment");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent in=getIntent();
        if(sharedPreferences != null)
        {
            token = sharedPreferences.getString("Token", null);
        }

        editTextAmount=(EditText)findViewById(R.id.editTextAmount);
        buttonPay=(Button) findViewById(R.id.buttonPay);
        String amt=in.getStringExtra("amount");

        editTextAmount.setText(amt+" Rs");

        editTextAmount.setFocusable(false);
        editTextAmount.setEnabled(false);
        editTextAmount.setCursorVisible(false);
        editTextAmount.setKeyListener(null);
        editTextAmount.setBackgroundColor(Color.TRANSPARENT);


        src=in.getStringExtra("source");
        dest=in.getStringExtra("destination");
        bus_no=in.getStringExtra("bus_no");

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editable pr=editTextAmount.getText();
                String price=pr.toString().trim();

                //Intent send = new newIntent(this, Result.classs);
                sharedPreferences.edit().putString("Token",token).commit();
                Intent send=new Intent(TicketActivity.this,RazorPayActivity.class);
                send.putExtra("price",amt);
                send.putExtra("source",src);
                send.putExtra("destination",dest);
                send.putExtra("bus_no",bus_no);

                startActivity(send);

            }
        });
    }
}