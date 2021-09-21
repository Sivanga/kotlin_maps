package com.example.kotlingooglemaps

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.kotlingooglemaps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import java.security.Permission
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val REQUEST_LOCATION_PERMISSION = 1


    private val TAG = MapsActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        map = googleMap

       val latLng = LatLng(51.507391, -0.207534)
        val zoomLever = 16f
       map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLever))

        val overlaySize = 100f
        val androidOverlay = GroundOverlayOptions().image(BitmapDescriptorFactory
            .fromResource(R.drawable.android))
            .position(latLng, overlaySize)
        map.addGroundOverlay(androidOverlay)
        map.addMarker(MarkerOptions().position(latLng))
        setMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)
        enableMyLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.normal_map -> map.mapType = (GoogleMap.MAP_TYPE_NORMAL)
            R.id.hybrid_map -> map.mapType = (GoogleMap.MAP_TYPE_HYBRID)
            R.id.satellite_map -> map.mapType = (GoogleMap.MAP_TYPE_SATELLITE)
            R.id.terrain_map -> map.mapType = (GoogleMap.MAP_TYPE_TERRAIN)
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setMapLongClick(map: GoogleMap){

        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_BLUE
            )).snippet(snippet))
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener{poi ->
            val poiMarker = map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
            poiMarker.showInfoWindow()
        }
    }

    private fun setMapStyle(map: GoogleMap){
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )
            if(!success){
                Log.e(TAG, "Style parsing failed")
            }
        }catch (e : Resources.NotFoundException){
            Log.e(TAG, e.message.toString())
        }
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat
            .checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat
                    .checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation(){
        if (isPermissionGranted()){
            map.isMyLocationEnabled = true
        }else{
            ActivityCompat.requestPermissions(this, arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode === REQUEST_LOCATION_PERMISSION){
           if (grantResults.size > 0 && (grantResults[0] === PackageManager.PERMISSION_GRANTED)){
               enableMyLocation()
           }
       }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }
}