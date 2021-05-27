package com.example.bususerapp.TicketBooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.bususerapp.LiveTrack.LiveMapsActivity;
import com.example.bususerapp.MainActivity;
import com.example.bususerapp.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class RazorPayActivity extends AppCompatActivity implements PaymentResultListener {

    Button btPay;
    JSONObject jsonObject;
    SharedPreferences sharedPreferences;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razor_pay);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent in=getIntent();
        if(sharedPreferences != null)
        {
            token = "token " + sharedPreferences.getString("Token", null);
        }
        Log.i("Tokennn", token);
        btPay=findViewById(R.id.bt_pay);

        Log.i("info",in.getStringExtra("source"));
        Log.i("info",in.getStringExtra("destination"));
        Log.i("info",in.getStringExtra("bus_no"));

        //Here we will make an API call to get the payment Amount
        String sAmount=in.getStringExtra("price");
        int amount=Math.round(Float.parseFloat(sAmount)*100);

        btPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Checkout checkout=new Checkout();
                checkout.setKeyID("rzp_test_KdGEymbGPAxgZE");
                checkout.setImage(R.drawable.rzp_logo);
                JSONObject object=new JSONObject();

                try {
                    object.put("name","Bus App");
                    object.put("description","Test Payment");
                    object.put("theme.color","#0093DD");
                    object.put("currency","INR");
                    object.put("amount",amount);
                    object.put("prefill.contact","9769788115");
                    object.put("prefill.email","gvipin081@gmail.com");

                    checkout.open(RazorPayActivity.this,object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {

        Intent in=getIntent();
        //String src=in.getStringExtra("source");
        //String dest=in.getStringExtra("destination");
        //String bus_no=in.getStringExtra("bus_no");
        //int price=Integer.parseInt(in.getStringExtra("price").substring(0,3).trim());
        //Log.i("info",(in.getStringExtra("price").substring(0,3)));
        //Log.i("info",src);
        //Log.i("info",dest);
        //Log.i("info",s);

        jsonObject = new JSONObject();

        try {
            String src=in.getStringExtra("source");
            String dest=in.getStringExtra("destination");
            String bus_no=in.getStringExtra("bus_no");
            String price=in.getStringExtra("price");

            jsonObject.put("bus_no",bus_no);
            jsonObject.put("source", src);
            jsonObject.put("price", price);
            jsonObject.put("mode", "Gpay");
            jsonObject.put("destination", dest);
            jsonObject.put("payment_id",s);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.initialize(getApplicationContext());

        AndroidNetworking.post("https://bestbus-api.herokuapp.com/api/ticket/")
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", token)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            Log.d("info",response.toString(4));
                            Toast.makeText(RazorPayActivity.this, "Ticket Booked Successfully!", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("info","kuch output aaya kya yaha..?.");
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.i("info","there is some error.");
                        Log.i("info",error.toString());

                    }
                });

        Intent send=new Intent(RazorPayActivity.this, MainActivity.class);
        startActivity(send);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

    }
}