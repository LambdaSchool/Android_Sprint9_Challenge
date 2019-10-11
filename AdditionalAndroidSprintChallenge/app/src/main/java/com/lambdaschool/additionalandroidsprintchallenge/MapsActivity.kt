package com.lambdaschool.additionalandroidsprintchallenge

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        private const val FINE_LOCATION_REQUEST_CODE = 5
        private const val MEDIA_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            FINE_LOCATION_REQUEST_CODE)

        mediaPlayer = MediaPlayer.create(this, R.raw.button_click_sound_effect)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Remove marker when clicked
        mMap.setOnMarkerClickListener { marker ->
            marker.remove()
            true
        }

        // Long press to set pin
        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(MarkerOptions().position(latLng))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mediaPlayer?.start()
        }

        // Add a marker for Lambda School and move the camera
        val lambdaLocation = LatLng(37.791580, -122.402280)
        mMap.addMarker(MarkerOptions().position(lambdaLocation).title("Lambda School"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lambdaLocation))
    }

    // Inflate toolbar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        // Centering User Location
        if (id == R.id.menu_location) {
            if (item.isChecked) {
                getLocation()
                item.isChecked = true

            } else {
                item.isChecked = false
                val location = LatLng(37.791580, -122.402280)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            }
        }

        // Add Marker
        if (id == R.id.menu_marker) {
            mMap.addMarker(MarkerOptions().position(mMap.cameraPosition.target))
            // Add audio track when pin is dropped
            mediaPlayer?.start()
        }

        // Add audio
        if (id == R.id.menu_set_audio) {
            val setAudioIntent = Intent(Intent.ACTION_GET_CONTENT)
            setAudioIntent.type = "audio/*"
            startActivityForResult(setAudioIntent, MEDIA_REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            locationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    val mylocation = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation))
                }
            }
        }
    }
}
