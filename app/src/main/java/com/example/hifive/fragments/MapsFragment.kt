package com.example.hifive.fragments


import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import com.example.hifive.adapters.EventInfoAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.location.Location

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import com.example.hifive.MapsViewModel
import com.example.hifive.Models.Post
import com.example.hifive.R
import com.example.hifive.databinding.FragmentMapsBinding

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
import com.squareup.picasso.Picasso


class MapsFragment : Fragment(), OnMapReadyCallback {

    private var mapFragment : SupportMapFragment?=null

    private lateinit var mMap: GoogleMap

    private var eventList = ArrayList<Post>()

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

        Log.d("MapsFragment", mapsVM.getMyLocation().toString())

        mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        binding.zooming.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, newZoom: Int, fromUser: Boolean) {
                // Update UI or perform actions based on the progress change
                if (::mMap.isInitialized) {
                    moveMap(mapsVM.getCurrentLocation(), newZoom.toFloat())
                    mapsVM.setZoom(newZoom.toFloat())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when the user starts moving the thumb
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when the user stops moving the thumb
            }

        })

    }

    override fun onMapReady(googleMap: GoogleMap) {

        //getLocation()
        mMap = googleMap

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapsVM.getCurrentLocation(), mapsVM.getZoom().toFloat()))

        mMap.setOnCameraIdleListener {
            // Get the center LatLng of the map when the camera stops moving
            mapsVM.setCurrentLocation(mMap.cameraPosition.target)
            mapsVM.setZoom(mMap.cameraPosition.zoom)
            Log.d("MapsFragment", mapsVM.getCurrentLocation().toString())
            // Do something with currentLatLng
        }

//        val animDuration = 2000L // Animation duration in milliseconds
//
//        // Define the animation
//        val anim = ObjectAnimator.ofFloat(binding.eventinfo, "translationY", binding.eventinfo.height.toFloat(), 0f)
//        anim.duration = animDuration
//
//        mMap.setOnMapClickListener {
//            anim.start()
//            binding.eventinfo.visibility = View.GONE
//        }

        Firebase.firestore.collection("posts").get().addOnSuccessListener {
            val tempList = ArrayList<Post>()
            eventList.clear()
            var ploc = LatLng(0.0,0.0)
            for ((index,i) in it.documents.withIndex()) {

                val post: Post = i.toObject<Post>()!!
                tempList.add(post)
                //Log.d("MapsFragment", tempList[index].eventLoc)
                val loc = convertStringToLatLng(tempList[index].eventLoc)
//                if (index == 0)
//                    ploc = loc
//                if (index == 1)
//                    Log.d("MapsFragment", "distance between ${ploc} and ${loc} = ${calcDistance(ploc, loc, "Miles")}")
                //Log.d("MapsFragment", loc.toString())
                val icon: BitmapDescriptor = if (tempList[index].eventType == "Social") {
                    BitmapDescriptorFactory.fromResource(R.drawable.markertest)
                } else if (tempList[index].eventType == "Business") {
                    BitmapDescriptorFactory.fromResource(R.drawable.markertestb)
                } else {
                    BitmapDescriptorFactory.fromResource(R.drawable.markertesto)
                }
                Picasso.get().load(tempList[index].postUrl).fetch()
                mMap.addMarker(MarkerOptions()
                    .position(loc)
                    .title(tempList[index].title)
                    //.snippet("${tempList[index].caption}@${tempList[index].postUrl}")
                    .snippet(tempList[index].postId)
                    .icon(icon))
            }
            eventList.addAll(tempList)
            val eventInfoAdapter = EventInfoAdapter(requireContext(), eventList)
            mMap.setInfoWindowAdapter(eventInfoAdapter)
            //Log.d("mapsf", postList.size.toString())
        }
        //Log.d("MapsFragment", "distance between ${postList[0].eventLoc} and ${postList[1].eventLoc} =")

        mMap.setOnMarkerClickListener { marker ->
            // Handle marker click event here
            //val markerTag = marker.tag
            // Use markerTag as needed

            // Example: Display a toast with the marker title
            //Toast.makeText(context, "Clicked marker: ${marker.title}", Toast.LENGTH_SHORT).show()
            Log.d("MapsFragment", "dist = ${calcDistance(mapsVM.getMyLocation(), LatLng(marker.position.latitude, marker.position.longitude), "Km")}")
            false // Return true to consume the event and prevent default behavior (such as showing info window)
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


    private fun moveMap(newLoc: LatLng, newZoom: Float) {
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.cameraPosition.zoom + zoom))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLoc, mapsVM.getZoom()))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(newZoom))
        Log.d("MapsFragment", "currentLoc = ${mMap.cameraPosition.zoom}")
    }


    private fun calcDistance(llng1: LatLng, llng2 : LatLng, type: String) : Float {
        val loc1 = Location("llng1")
        val loc2 = Location("llng2")
        loc1.latitude = llng1.latitude
        loc2.latitude = llng2.latitude
        loc1.longitude = llng1.longitude
        loc2.longitude = llng2.longitude
        return if (type == "Miles")
            loc1.distanceTo(loc2) / 1609.344f
        else if (type == "Km")
            loc1.distanceTo(loc2) / 1000f
        else
            loc1.distanceTo(loc2)

    }


}