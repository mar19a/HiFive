package com.example.hifive.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hifive.Models.Post
import com.example.hifive.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.hifive.databinding.ActivityMapsBinding
import com.example.hifive.databinding.FragmentMapsBinding
import com.example.hifive.utils.POST
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var mapFragment : SupportMapFragment?=null

    private lateinit var mMap: GoogleMap

    private var postList = ArrayList<Post>()

    private lateinit var binding: FragmentMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(42.3601, -71.0589)))
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(7.0f))
        var yours = LatLng(42.3601, -71.0589)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yours, 15f))

        Firebase.firestore.collection(POST).get().addOnSuccessListener {
            var tempList = ArrayList<Post>()
            postList.clear()
            for ((index,i) in it.documents.withIndex()) {

                var post: Post = i.toObject<Post>()!!
                tempList.add(post)
                Log.d("mapsf", tempList[index].eventLoc)
                var loc = convertStringToLatLng(tempList[index].eventLoc)
                Log.d("mapsf", loc.toString())
                var icon: BitmapDescriptor
                if (tempList[index].eventType == "Social") {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.markertest)
                } else if (tempList[index].eventType == "Business") {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.markertestb)
                }
                else {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.markertesto)
                }
                mMap.addMarker(MarkerOptions()
                    .position(loc)
                    .title(tempList[index].title)
                    .snippet(tempList[index].caption)
                    .icon(icon))
            }
            postList.addAll(tempList)
            Log.d("mapsf", postList.size.toString())
        }
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