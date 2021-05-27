package com.example.bususerapp.Geofence;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.bususerapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.bususerapp.Geofence.Point;
import com.example.bususerapp.Geofence.Polygon;

public class GeofenceActivity extends AppCompatActivity{

    GoogleMap mMap;
    NotificationHelper notificationHelper;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);
    }

    public void getCurrentLocation(){
        FirebaseApp secondApp = FirebaseApp.getInstance("busdriverapp-258fb");
        FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
        //Location location = null;
        //location.setLatitude(lat);
        //location.setLongitude(lon);

        //Double lon = 72.9999919,lat = 19.1576469;
        //textView.setText("Latitude");
        databaseReference = secondDatabase.getReference("Location").child("Rishi");

        //LatLng latLng = new LatLng(lat, lon);


        databaseReference.addChildEventListener(new ChildEventListener() {
            Double latitude, longitude;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.i("Updated data", dataSnapshot.getValue().toString());

                if(dataSnapshot.getKey().equals("latitude"))
                {
                    latitude = dataSnapshot.getValue(Double.class);
                    Log.i("Latitude", latitude.toString());
                }
                if(dataSnapshot.getKey().equals("longitude")){
                    longitude = dataSnapshot.getValue(Double.class);
                    Log.i("longitude", longitude.toString());
                }

                if(latitude != null && longitude!= null)
                {
                    LatLng latLng = new LatLng(latitude, longitude);
                    checkRoute(latLng);

                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void addPolygon(LatLng latLng)
    {
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(new LatLng(19.119342, 72.846251))//A
                .add(new LatLng(19.119826, 72.846314))//B
                .add(new LatLng(19.1198437,72.8459988))//C
                .add(new LatLng(19.128154, 72.847611))//D
                .add(new LatLng(19.1290813,72.844292))//E
                .add(new LatLng(19.129581, 72.842952))//F
                .add(new LatLng(19.132352, 72.843272))//G
                .add(new LatLng(19.1324292,72.8420208))//H
                .add(new LatLng(19.132116, 72.842328))//I
                .add(new LatLng(19.129653, 72.842127))//J
                .add(new LatLng(19.128766, 72.842923))//K
                .add(new LatLng(19.128766,72.8420332))//L
                .add(new LatLng(19.127406, 72.846603))//M
                .add(new LatLng(19.1202042,72.844979))//N
                .add(new LatLng(19.119664, 72.846118))//O
                .add(new LatLng(19.119076, 72.845842))//P
                .add(new LatLng(19.119342, 72.846251));//A

        Polyline polyline = mMap.addPolyline(polylineOptions);

    }

    private void checkRoute(LatLng latLng){

        Polygon polygon = Polygon.Builder()
                .addVertex(new Point(19.119342, 72.846251))//A
                .addVertex(new Point(19.119692, 72.846251))//B. This point doesn't fail the test anymore
                .addVertex(new Point(19.1198437,72.8459988))//C
                .addVertex(new Point(19.128154, 72.847611))//D
                .addVertex(new Point(19.1290813,72.844292))//E
                .addVertex(new Point(19.129581, 72.842952))//F
                .addVertex(new Point(19.132352, 72.843272))//G
                .addVertex(new Point(19.1324292,72.8420208))//H
                .addVertex(new Point(19.132116, 72.842328))//I
                .addVertex(new Point(19.129653, 72.842127))//J
                .addVertex(new Point(19.128766, 72.842923))//K
                .addVertex(new Point(19.128766,72.8420332))//L
                .addVertex(new Point(19.127406, 72.846603))//M
                .addVertex(new Point(19.1202042,72.844979))//N
                .addVertex(new Point(19.119664, 72.846118))//O
                .addVertex(new Point(19.119622, 72.845796))//P
                .addVertex(new Point(19.119622, 72.845796))//Q
                .addVertex(new Point(19.119176, 72.845860))//A
                .build();

        Point point = new Point(latLng.latitude, latLng.longitude);
        boolean contains = polygon.contains(point);
        //notificationHelper = new NotificationHelper(this);
        if(contains == true)
        {
            Toast.makeText(this, "Inside polygon", Toast.LENGTH_LONG).show();
            Log.i("Is inside", "true");
            notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", GeofenceActivity.class);
        }
        else
        {
            Toast.makeText(this, "Outside polygon", Toast.LENGTH_LONG).show();
            Log.i("Is inside", "False");
            notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", GeofenceActivity.class);
        }

    }
}