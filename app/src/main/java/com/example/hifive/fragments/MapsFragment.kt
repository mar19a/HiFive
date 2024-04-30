package com.example.hifive.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.location.Location
import android.widget.Toast

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

        binding.zooming.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Update UI or perform actions based on the progress change
                updateCameraZoom(progress)
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapsVM.getLocation()!!, 15f))

        Firebase.firestore.collection("posts").get().addOnSuccessListener {
            val tempList = ArrayList<Post>()
            postList.clear()
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
                mMap.addMarker(MarkerOptions()
                    .position(loc)
                    .title(tempList[index].title)
                    .snippet(tempList[index].caption)
                    .icon(icon))
            }
            postList.addAll(tempList)
            //Log.d("mapsf", postList.size.toString())
        }
        //Log.d("MapsFragment", "distance between ${postList[0].eventLoc} and ${postList[1].eventLoc} =")

        mMap.setOnMarkerClickListener { marker ->
            // Handle marker click event here
            //val markerTag = marker.tag
            // Use markerTag as needed

            // Example: Display a toast with the marker title
            //Toast.makeText(context, "Clicked marker: ${marker.title}", Toast.LENGTH_SHORT).show()

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


    private fun updateCameraZoom(zoom: Int) {
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.cameraPosition.zoom + zoom))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom.toFloat()))
        Log.d("MapsFragment", mMap.cameraPosition.zoom.toString())
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