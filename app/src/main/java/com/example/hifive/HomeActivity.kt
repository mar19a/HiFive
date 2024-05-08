package com.example.hifive

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.Manifest
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hifive.databinding.ActivityHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class HomeActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var mapsVM: MapsViewModel

    private lateinit var binding: ActivityHomeBinding

    private val default = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each

        navView.setupWithNavController(navController)

        mapsVM = ViewModelProvider(this)[MapsViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //mapsVM.findLocation(fusedLocationClient)
        if (hasLocationPermission()) {
            // Permissions already granted, proceed to retrieve location
            findLocation()
            Log.d("HomeActivity2", "has perms: location is: ${mapsVM.getMyLocation()}")
        } else {
            // Request location permissions
            requestLocationPermission()
            Log.d("HomeActivity2", "request: location is: ${mapsVM.getMyLocation()}")
        }

    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed to retrieve location
                findLocation()
            } else {
                // Permissions denied, using default location
                mapsVM.setMyLocation(default)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun findLocation() {

        var location = LatLng(default.latitude, default.longitude)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { loc ->
                // Got last known location. In some rare situations this can be null.
                if (loc != null) {
                    location =  LatLng(loc.latitude, loc.longitude)
                    mapsVM.setMyLocation(location)
                    mapsVM.setCurrentLocation(location)
                    Log.d("HomeActivity2", location.toString())
                } else {
                    mapsVM.setMyLocation(location)
                    mapsVM.setCurrentLocation(location)
                    Log.d("HomeActivity2", "null")
                }
            }
            .addOnFailureListener { e ->
                // Handle failure to retrieve location
                mapsVM.setMyLocation(location)
                mapsVM.setCurrentLocation(location)
                Log.e("HomeActivity2", "Error getting location: ${e.message}", e)
            }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

}