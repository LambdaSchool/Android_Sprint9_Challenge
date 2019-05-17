package com.example.android_sprint9_challenge;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int FINE_LOCATION_REQUEST_CODE = 5;
    Context context;
    Button iconFindLocation;
    Button iconDropPin;
    int     pin = 0;
    private GoogleMap mMap;
    private LatLng    latLng;
    private String lastClick = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        iconDropPin = findViewById(R.id.icon_drop_pin);
        iconFindLocation = findViewById(R.id.icon_find_location);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setActionBar(toolbar);
        context = this;
        iconDropPin.setActivated(false);
        iconFindLocation.setActivated(false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        //setup map and activate toolbar buttons
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        iconDropPin.setActivated(true);
        iconFindLocation.setActivated(true);


        //drop pin on long click on map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                dropPin(latLng);
            }
        });

        //find location toolbar icon listener
        iconFindLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        //drop pin toolbar icon listener
        iconDropPin.setSoundEffectsEnabled(false);
        iconDropPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LatLng cameraCenter = mMap.getCameraPosition().target;
                dropPin(cameraCenter);
            }
        });

        //one click selects pin, two clicks removes it
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(lastClick.equals(marker.getId())) {
                    marker.remove();
                    return true;
                }
                lastClick = marker.getId();
                return false;
            }
        });
    }


    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // request the permission
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    @Override //handles getting permissions
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {

                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }

    private void dropPin(LatLng location) {
        mMap.addMarker(new MarkerOptions().position(location).title("Your Location " + pin++));
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.boink);
        mediaPlayer.start();
    }

    @Override //inflates menu on toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override//handles selected items on toolbar
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_find_location) {
            getCurrentLocation();
        }

        if (id == R.id.menu_drop_pin) {

            final LatLng cameraCenter = mMap.getCameraPosition().target;
            dropPin(cameraCenter);
        }

        return super.onOptionsItemSelected(item);
    }
}