package com.example.hifive

import android.R.attr.text
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import kotlin.properties.Delegates


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var addr: String
    private lateinit var latlong: String
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var marker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(9.0f))
        var yours = LatLng(42.3601, -71.0589)
        //getAddress(llng)
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(42.3601, -71.0589)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yours, 15f))

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

}