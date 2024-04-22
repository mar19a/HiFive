package com.example.hifive

import android.content.Intent

import android.location.Geocoder

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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

    private lateinit var addr: String

    private lateinit var latlong: String

    private lateinit var mMap: GoogleMap

    private lateinit var binding: ActivityMapsBinding

    private lateinit var userLocation: String

    private val default = LatLng(0.0, 0.0)

    private var marker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userLocation = intent.getStringExtra("location").toString()
        Log.d("MapsActivity", userLocation)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.send.setOnClickListener() {
            val intent = Intent(this@MapsActivity, PostActivity::class.java)
            intent.putExtra("address", addr)
            intent.putExtra("latlong", latlong)
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
            marker = googleMap.addMarker(MarkerOptions().position(llng).title("Event")) //placeholder
            //addr = "Los Angeles" //placeholder
            addr = if (getAddress(llng) != null)
                getAddress(llng).toString()
            else
                ""
            latlong = "${llng.latitude},${llng.longitude}"
            binding.send.isEnabled = true
        }

    }

    private fun getAddress(llng: LatLng) : String? {

        val geocoder = Geocoder(this, Locale.getDefault())
        var address : String? = ""
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            geocoder.getFromLocation(
//                llng.latitude,
//                llng.longitude,
//                1
//            ) {
//                if (it.isNotEmpty()) {
//                    address = it[0]?.getAddressLine(0)
//                    // Use the obtained address
//                    Toast.makeText(this, address, Toast.LENGTH_SHORT).show()
//                } else {
//                    // No address found
//                    Toast.makeText(this, "No address found for the location", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//        } else {
//            try {
//                val addresses = geocoder.getFromLocation(
//                    llng.latitude,
//                    llng.longitude,
//                    1
//                )
//                if (addresses != null) {
//                    if (addresses.isNotEmpty()) {
//                        address = addresses[0]?.getAddressLine(0)
//                        // Use the obtained address
//                        Toast.makeText(this, address, Toast.LENGTH_SHORT).show()
//                    } else {
//                        // No address found
//                        Toast.makeText(this, "No address found for the location", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//                Toast.makeText(this, "Error getting address", Toast.LENGTH_SHORT).show()
//            }
//        }
//        return address
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
                    Toast.makeText(this, "No address found for the location", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error getting address", Toast.LENGTH_SHORT).show()
        }
        return address
    }

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