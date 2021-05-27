package com.example.bususerapp.LiveTrack;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bususerapp.R;
import com.example.bususerapp.databinding.ActivityLiveMapsBinding;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.bususerapp.Geofence.Polygon;
import com.example.bususerapp.Geofence.Point;
import com.example.bususerapp.Geofence.Line;


public class LiveMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private ActivityLiveMapsBinding binding;
    DatabaseReference databaseReference, databaseReference1;
    SharedPreferences sharedPreferences;
    TextView textView;
    String username;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        textView = (TextView) findViewById(R.id.longitude);
        Intent intent = getIntent();
        if(sharedPreferences != null)
        {
            username = sharedPreferences.getString("username", null);
        }
        Log.i("UserNameeee: ", username);



        binding = ActivityLiveMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        //zoomToUserLocation();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
            //zoomToUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else  {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }

        }
        FirebaseApp secondApp2 = FirebaseApp.getInstance("busdriverapp-258fb");
        FirebaseDatabase secondDatabase2 = FirebaseDatabase.getInstance(secondApp2);
        databaseReference1 = secondDatabase2.getReference("Location");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double latitude = snapshot.child(username).child("latitude").getValue(Double.class);
                Double longitude = snapshot.child(username).child("longitude").getValue(Double.class);
                //sharedPreferences.edit().putString("Latitude", latitude).apply();
                //sharedPreferences.edit().putString("LongitudeS", longitude).apply();
                if(latitude!=null && longitude!=null) {
                    Log.i("Latitude isss::::", longitude.toString());
                    LatLng latLng = new LatLng(latitude, longitude);
                    Location location = new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    setUserLocationMarker(location);
                    addPolygon(latLng);
                    checkRoute(latLng);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseReference1.addValueEventListener(postListener);

        getCurrentLocation();

    }

    public void getCurrentLocation(){
        FirebaseApp secondApp = FirebaseApp.getInstance("busdriverapp-258fb");
        FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
        //Location location = null;
        //location.setLatitude(lat);
        //location.setLongitude(lon);

        //Double lon = 72.9999919,lat = 19.1576469;
        //textView.setText("Latitude");
        databaseReference = secondDatabase.getReference("Location").child(username);

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
                    Location location = new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    setUserLocationMarker(location);
                    addPolygon(latLng);
                    checkRoute(latLng);
                }
                //Log.i("Prev", prevChildKey);
                //mMap.addMarker((new MarkerOptions().position(latLng).title("Current Location")));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng, 15));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redcar));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        } else  {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }

        if (userLocationAccuracyCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(32, 255, 0, 0));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle = mMap.addCircle(circleOptions);
        } else {
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }

    private void zoomToUserLocation(Location location) {
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
//                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
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
            //notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", GeoFenceActivity.class);
        }
        else
        {
            Toast.makeText(this, "Outside polygon", Toast.LENGTH_LONG).show();
            Log.i("Is inside", "False");
            //notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", GeoFenceActivity.class);
        }

    }
}