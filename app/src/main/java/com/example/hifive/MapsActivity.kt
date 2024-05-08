package com.example.hifive

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import com.example.hifive.Post.PostActivity
import com.example.hifive.databinding.ActivityMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.Locale


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
//    companion object {
//        private const val PERMISSIONS_REQUEST_LOCATION = 101
//    }
    private var addr = ""

    private lateinit var latlong: String

    private lateinit var mMap: GoogleMap

    private lateinit var binding: ActivityMapsBinding

    private var userLocation = LatLng(0.0, 0.0).toString()

    //private val default = LatLng(0.0, 0.0)

    private var marker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userLocation = intent.getStringExtra("location").toString()
        Log.d("MapsActivity", "User location received: $userLocation")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.zooming.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, newZoom: Int, fromUser: Boolean) {
                // Update UI or perform actions based on the progress change
                if (::mMap.isInitialized) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(newZoom.toFloat()))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when the user starts moving the thumb
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when the user stops moving the thumb
            }

        })

        binding.send.setOnClickListener {
            val intent = Intent(this@MapsActivity, PostActivity::class.java).apply {
                putExtra("address", addr)
                putExtra("latlong", latlong)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
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
//        if (default != null) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default, 15f))
//        } else {
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default, 15f))
//        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertStringToLatLng(userLocation), 15f))

        googleMap.setOnMapClickListener { llng ->
            // Add a marker at the tapped location
            marker?.remove()
            marker = googleMap.addMarker(MarkerOptions().position(llng).title("Event"))

            addr = if (getAddress(llng) != null)
                getAddress(llng).toString()
            else
                ""
            if (addr != "") {
                latlong = "${llng.latitude},${llng.longitude}"
                binding.send.isEnabled = true
            } else {
                binding.send.isEnabled = false
            }
        }

    }

//    private fun checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                AlertDialog.Builder(this)
//                    .setTitle("Location Permission Needed")
//                    .setMessage("This app needs the Location permission, please accept to use location functionality")
//                    .setPositiveButton("OK") { _, _ ->
//                        ActivityCompat.requestPermissions(this,
//                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                            PERMISSIONS_REQUEST_LOCATION)
//                    }
//                    .create()
//                    .show()
//            } else {
//                ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                    PERMISSIONS_REQUEST_LOCATION)
//            }
//        } else {
//            if (userLocation.isNotBlank()) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertStringToLatLng(userLocation), 15f))
//            } else {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default, 15f))
//            }
//        }
//    }
    
    private fun getAddress(llng: LatLng) : String? {

        val geocoder = Geocoder(this, Locale.getDefault())
        var address : String? = ""
        try {
            val addresses = geocoder.getFromLocation(
                llng.latitude,
                llng.longitude,
                1
            )
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    address = addresses[0]?.getAddressLine(0)
                    // Use the obtained address
                    Toast.makeText(this, address, Toast.LENGTH_SHORT).show()
                } else {
                    // No address found
                    Toast.makeText(this, getString(R.string.no_address_found), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.address_error), Toast.LENGTH_SHORT).show()
        }
        return address
    }

// This activity doesn't make any location requests. That is done in the HomeActivity

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // Call the superclass method
//
//        when (requestCode) {
//            PERMISSIONS_REQUEST_LOCATION -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertStringToLatLng(userLocation), 15f))
//                    }
//                } else {
//                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }

//    private fun convertStringToLatLng(latlngString: String): LatLng {
////        if (latlngString.isNullOrEmpty() || !latlngString.contains(",")) {
////            Log.e("MapsActivity", "Invalid LatLng string: '$latlngString'")
////            return default
////        }
//        val latlngParts = latlngString.split(",").map { it.trim() }
//        if (latlngParts.size == 2) {
//            try {
//                val latitude = latlngParts[0].toDouble()
//                val longitude = latlngParts[1].toDouble()
//                return LatLng(latitude, longitude)
//            } catch (e: NumberFormatException) {
//                Log.e("MapsActivity", "Error parsing latitude or longitude from string: '$latlngString'")
//                return default
//            }
//        }
//        return default
//    }
    private fun convertStringToLatLng(latlngString: String): LatLng {
        // Split the string into latitude and longitude parts
        val latlngParts = latlngString.split(",")

        // Check if the string has both latitude and longitude parts
        require(latlngParts.size == 2) { "Invalid LatLng string format: $latlngString" }

        try {
            // Parse latitude and longitude values from string parts
            val latitude = latlngParts[0].toDouble()
            val longitude = latlngParts[1].toDouble()

            // Create and return a new LatLng object
            return LatLng(latitude, longitude)
        } catch (e: NumberFormatException) {
            // Handle parsing errors
            throw IllegalArgumentException("Invalid latitude or longitude value in LatLng string: $latlngString")
        }
    }


}