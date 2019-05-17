package com.example.pindropper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
	
	private GoogleMap mMap;
	Context context;
	Button btnAddPin;
	Button btnGoToYou;
	MediaPlayer MPpinDrop;
	
	public static final int PERMISSIONS_REQUEST_LOCATION = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		
		context = this;
		
		MPpinDrop = MediaPlayer.create(this,R.raw.pindrop);
		
		btnAddPin = findViewById(R.id.btn_add_pin);
		btnGoToYou = findViewById(R.id.btn_current_location);
		
		btnAddPin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).title("Marker Here!"));
				MPpinDrop.start();
			}
		});
		
		
		//check for permission
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
		} else {
			getLocation();
		}
		
		
	}
	
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
			if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				getLocation();
			} else {
				//permission denied
			}
		}
	}
	
	private void getLocation() {
		//check again before using permission
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
		}
		
		FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
		locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
			@Override
			public void onSuccess(Location location) {
				
				final Location finalLocation = location;
				
				btnGoToYou.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						LatLng userLocation = new LatLng(finalLocation.getLatitude(), finalLocation.getLongitude());
						mMap.addMarker(new MarkerOptions().position(userLocation).title("You are Here!"));
						mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
					}
				});
				
				
			}
		});
		
	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
	}
}
