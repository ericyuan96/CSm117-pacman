package com.example.ericyuan.googlemaptest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

////////////////////////////////////////////////////////////////////////
//
//  NOTE: Code for handling current location and
//  permission requests comes from this tutorial:
//  https://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android
//
//  The "What's next" section also contains some potentially useful material
//
////////////////////////////////////////////////////////////////////////


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    int circleFlag = 0;
    ArrayList<Circle> circles = new ArrayList<Circle>();
    int numcircles = 0;
    int count = 0;
    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    int distanceFlag = 0;
    int distanceTraveled = 0;

    int scoreValue = 0;
    TextView distance;
    TextView score;

    static int timer = 60;
    TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //request location permission on startup
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        /*try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }*/

        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        //SETS COLOR
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        /*
        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(mLocation.getLatitude() + .000001, mLastLocation.getLongitude()))
                .radius(1); // In meters

        // Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);*/

        //****CIRCLE CODE****
        for (int i = 0; i < 5; i++) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(34.0689313 + (.0001*i), -118.4426616))
                    .radius(10).fillColor(0xffffe5ee).strokeColor(0xfffff0f0); // In meters

            circles.add(mMap.addCircle(circleOptions));
            numcircles += 1;
        }

        //****display distance****
        distance = (TextView)findViewById(R.id.distance);
        distance.setTextColor(Color.WHITE);
        distance.setText("Distance: 0m");

        //****score****
        score = (TextView)findViewById(R.id.score);
        score.setTextColor(Color.WHITE);
        score.setText("Score: 0");

        //****END****
        String points="points=34.1,-118.4|35.1,-117.4";
        String url = "https://roads.googleapis.com/v1/nearestRoads?" + points + "&key=" + "AIzaSyDPyt3wOffBv5jmCcqjuwwF15TXDfeuRD4";

        Log.d("request", url);
        try {
            HTTPRequest( url );
        }
        catch(IOException e)
        {}

        //****timer****
        /*time = (TextView)findViewById(R.id.time);
        distance.setTextColor(Color.WHITE);

        while (true) {
            timer--;
            distance.setText("Time: "+timer+"s");
            if (timer == 0) {
                Intent i = new Intent(this, EndActivity.class);
                startActivity(i);
            }

            try {
                Thread.sleep(1000);
            }
            catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }*/
    }

    //send http requests for google maps
    public String HTTPRequest( String url_string ) throws IOException
    {
        //use this since you can't do network tasks in main thread
        //may be a jank fix but whatevers
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        URL url = new URL(url_string);

        String result;

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        } finally {
            urlConnection.disconnect();
        }
        Log.d("response", result);
        return result;
    }

    //read the input stream from http requests
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        /*count++;
        Log.d("LOCATION COUNT", String.valueOf(count));*/
        if (distanceFlag > 0){
            distanceTraveled += mLastLocation.distanceTo(location);
            distance.setText("Distance: " + Integer.toString(distanceTraveled) + "m");
        }
        else
            distanceFlag++;

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.raw.pacman));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

        //circle hit
        /*if (Math.abs(mLastLocation.getLatitude() - circles.get(0).getCenter().latitude) < .0001
                && Math.abs(mLastLocation.getLongitude() - circles.get(0).getCenter().longitude) < .0001)
            circles.get(0).remove();*/

        Location myLocation = new Location("");
        myLocation.setLatitude(mCurrLocationMarker.getPosition().latitude);
        myLocation.setLongitude(mCurrLocationMarker.getPosition().longitude);


        //Log.d( Double.toString( myLocation.getLatitude() ) );
        //Log.d( "lat", String.valueOf( myLocation.getLatitude() ) );
        //Log.d( "long", String.valueOf( myLocation.getLongitude() ) );

        Location circLocation = new Location("");
        for (int j = 0; j < numcircles; j++) {
            circLocation.setLatitude(circles.get(j).getCenter().latitude);
            circLocation.setLongitude(circles.get(j).getCenter().longitude);
            if (myLocation.distanceTo(circLocation) < 10) {
                //circles.set(0, null);
                circles.get(j).remove();
                scoreValue = (numcircles - circles.size()) * 100;
                score.setText("Score: "+scoreValue);
            }
        }


        //stop location updates
        /*if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //request permission from the user to enable location
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    //handle result of user being asked for location permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }
    public void endScreen(View view){
        Intent intent = new Intent(this, EndActivity.class);
        intent.putExtra("map.score", scoreValue);
        intent.putExtra("map.distance", distanceTraveled);
        startActivity(intent);
    }
}
