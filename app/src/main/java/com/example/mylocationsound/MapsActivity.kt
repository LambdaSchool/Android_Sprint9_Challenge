package com.example.mylocationsound

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat

import androidx.drawerlayout.widget.DrawerLayout

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView

abstract class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    abstract var buttonFindLocation: NavigationView
    private lateinit var  mapFragment: SupportMapFragment
    private lateinit var gpsTracker: GpsTracker
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private lateinit var location: Location
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1
    private val MIN_TIME_BET_UPDATES = (1000 * 60 * 1).toLong()
    private lateinit var locationManager: LocationManager
    lateinit var handler: Handler
    val ACCESS_FINE_LOCATION = 1
    val ACCESS_COARSE_LOCATION= 2

    private var drawerLayout: DrawerLayout? = null

    private var currentType: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // this will assign our toolbar xml to be the system toolbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
      //  setSupportActionBar(toolbar)
        toolbar.title = title
        // TODO 3: get handle to drawer layout and bind to toolbar toggle
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when (menuItem.itemId){
                R.id.mSeeMyLocation -> {
                 loadMapData()
                }
            }
            true
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    private fun loadMapData() {
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        gpsTracker = GpsTracker(this)
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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION
            )
        } else {
            gpsTracker(googleMap)
            //googleMap= googleMap1
        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    private fun gpsTracker(googleMap1: GoogleMap){
        if (gpsTracker.canGetLoaction()) {
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            gpsTracker.onLocationChanged(location)

            latitude = gpsTracker.getLatitude()
            longitude = gpsTracker.getLongitude()

            mMap = googleMap1

            // Add a marker in Sydney and move the camera
            Handler().postDelayed({
                val pune = LatLng(latitude, longitude)
                mMap.addMarker(MarkerOptions().position(pune).title("Marker in oh no, not where I am at but darn close"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pune))
                mMap.animateCamera(CameraUpdateFactory.zoomIn())
                mMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f))
                //mMap.setMaxZoomPreference(14.0f);
                mMap.maxZoomLevel
            }, 1500)

            val builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setTitle("Location")
            builder.setMessage("This is your current location: Latitude: $latitude Longitude: $longitude")
            builder.setPositiveButton("OK") { dialogInterface, i -> dialogInterface.dismiss() }
            builder.show()
        } else {
            gpsTracker.openSettings()//Open the settings alert to enable the GPS sevice

        }

    }
    override fun onRequestPermissionsResult(permsRequestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (permsRequestCode) {
            0 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_COARSE_LOCATION)        //gpsTracker()
                }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Show an explanation to the user *asynchronously*
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)        //gpsTracker()
                    }else{
                        val builder = AlertDialog.Builder(this)
                        builder.setCancelable(true)
                        builder.setTitle("Alert")
                        builder.setMessage("Location permission is required")
                        builder.setPositiveButton("OK") { dialogInterface, i ->
                            dialogInterface.dismiss() }
                        builder.show()
                    }
                }
            }
            1 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadMapData()
                }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        //Show an explanation to the user *asynchronously*
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_COARSE_LOCATION)        //gpsTracker()
                    }else{
                        val builder = AlertDialog.Builder(this)
                        builder.setCancelable(true)
                        builder.setTitle("Alert")
                        builder.setMessage("Location permission is required")
                        builder.setPositiveButton("OK") { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }
                        builder.show()
                    }
                }
            }
        }
    }
}
