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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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