package com.example.hifive.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import com.example.hifive.MapsViewModel
import com.example.hifive.Models.Post
import com.example.hifive.R
import com.example.hifive.databinding.FragmentMapsBinding
import com.example.hifive.utils.POST

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class MapsFragment : Fragment(), OnMapReadyCallback {

    private var mapFragment : SupportMapFragment?=null

    private lateinit var mMap: GoogleMap

    //private val default = LatLng(0.0, 0.0)

    private var postList = ArrayList<Post>()

    private val mapsVM: MapsViewModel by activityViewModels()

    private lateinit var binding: FragmentMapsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("MapsFragment", mapsVM.getLocation().toString())

        mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        binding.zoomIn.setOnClickListener {
            // Increase the zoom level
            updateCameraZoom(1)
        }

        binding.zoomOut.setOnClickListener {
            // Increase the zoom level
            updateCameraZoom(-1)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        //getLocation()

        mMap = googleMap
//        val loc = mapsVM.getLocation()
//        if (loc != null) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f))
//        } else {
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default, 15f))
//        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapsVM.getLocation()!!, 15f))


        Firebase.firestore.collection("posts").get().addOnSuccessListener {
            val tempList = ArrayList<Post>()
            postList.clear()
            for ((index,i) in it.documents.withIndex()) {

                val post: Post = i.toObject<Post>()!!
                tempList.add(post)
                //Log.d("MapsFragment", tempList[index].eventLoc)
                val loc = convertStringToLatLng(tempList[index].eventLoc)
                //Log.d("MapsFragment", loc.toString())
                val icon: BitmapDescriptor = if (tempList[index].eventType == "Social") {
                    BitmapDescriptorFactory.fromResource(R.drawable.markertest)
                } else if (tempList[index].eventType == "Business") {
                    BitmapDescriptorFactory.fromResource(R.drawable.markertestb)
                } else {
                    BitmapDescriptorFactory.fromResource(R.drawable.markertesto)
                }
                mMap.addMarker(MarkerOptions()
                    .position(loc)
                    .title(tempList[index].title)
                    .snippet(tempList[index].caption)
                    .icon(icon))
            }
            postList.addAll(tempList)
            //Log.d("mapsf", postList.size.toString())
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


    private fun updateCameraZoom(zoom: Int) {
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.cameraPosition.zoom + zoom))
    }


}