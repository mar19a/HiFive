package com.example.hifive

import android.R.attr.text
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(42.3601, -71.0589)))

        googleMap.setOnMapClickListener { latLng ->
            //implement some geocoding
            //val geocoder = Geocoder(this)
            //val addresses: MutableList<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            // Add a marker at the tapped location
            marker?.remove()
            marker = googleMap.addMarker(MarkerOptions().position(latLng).title("Event")) //placeholder
            addr = "Los Angeles" //placeholder
            latlong = "${latLng.latitude},${latLng.longitude}"
        }

    }

}