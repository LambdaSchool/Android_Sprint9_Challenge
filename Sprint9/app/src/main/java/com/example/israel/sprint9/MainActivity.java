package com.example.israel.sprint9;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SettingsFragment.OnSelectAudioInterface {

    private static final int REQUEST_MARKER_DROP_AUDIO = 1;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MediaPlayer mediaPlayer;
    private SettingsFragment.OnAudioSelectedListener audioSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_main_map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MARKER_DROP_AUDIO) {
            audioSelectedListener.onAudioSelected(data.getData());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.main_toolbar_center: {
                center();
            } break;

            case R.id.main_toolbar_add_marker: {
                addMarker();
            } break;

            case R.id.main_toolbar_settings: {
                SettingsFragment settingsFragment = SettingsFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.activity_main_constraint_root, settingsFragment)
                        .addToBackStack(null)
                        .commit();
            } break;
        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private void center() {
        if (googleMap == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void addMarker() {
        if (googleMap == null) {
            return;
        }

        googleMap.addMarker(new MarkerOptions().position(googleMap.getCameraPosition().target).title("My location"));

        playPinDropSound();
    }

    private void playPinDropSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        Uri audioUri = MarkerDropAudioSPDAO.getMarkerDropAudio(this);
        //Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pin_drop)
        if (audioUri == null) {
            return;
        }

        mediaPlayer = MediaPlayer.create(this, audioUri);
        mediaPlayer.start();

    }

    @Override
    public void onSelectAudio(SettingsFragment.OnAudioSelectedListener l) {
        audioSelectedListener = l;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");

        ComponentName componentName = intent.resolveActivity(getPackageManager());
        if (componentName != null) {
            startActivityForResult(intent, REQUEST_MARKER_DROP_AUDIO);
        } else {
            Toast toast = Toast.makeText(this, "No activity", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
