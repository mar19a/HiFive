package com.example.hifive.fragments


import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.hifive.MapsViewModel
import com.example.hifive.Models.Post
import com.example.hifive.R
import com.example.hifive.adapters.EventInfoAdapter
import com.example.hifive.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class MapsFragment : Fragment(), OnMapReadyCallback {

    private var mapFragment : SupportMapFragment?=null

    private lateinit var mMap : GoogleMap

    private var circle: Circle? = null

    private var eventList = ArrayList<Post>()

    private var markerList = ArrayList<Marker>()

    private var distMarkerList = ArrayList<Marker>()

    private var typeMarkerList = ArrayList<Marker>()

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

        val distanceList: AutoCompleteTextView = binding.dlist
        distanceList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ -> // Perform action based on the selected item
                if (::mMap.isInitialized) {
                    circle?.remove()
                    val mDist = getDistance(parent.getItemAtPosition(position).toString())
                    val circleOptions = CircleOptions()
                        .center(mapsVM.getMyLocation())
                        .radius(mDist) // In meters
                        .fillColor(R.color.purple_900)

                    Log.d("MapsFragment", "center=${mapsVM.getMyLocation()}, radius(mDist)=${mDist}")

                    for (marker in markerList) {
                        // Do something with each marker in the list
                        val myDist = calcDistance(
                            mapsVM.getMyLocation(),
                            LatLng(marker.position.latitude, marker.position.longitude)
                        )
                        Log.d("MapsFragment", "${marker}=${myDist}")
                        if (myDist > mDist && mDist != 0.0) {
                            marker.isVisible = false
                            distMarkerList.remove(marker)
                        } else {
                            if (typeMarkerList.contains(marker)) {
                                marker.isVisible = true
                            }
                            distMarkerList.add(marker)
                        }
                    }
                    circle = mMap.addCircle(circleOptions)
                }
            }

        val timeList: AutoCompleteTextView = binding.tlist
        timeList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ -> // Perform action based on the selected item
                if (::mMap.isInitialized) {
                    val mType = parent.getItemAtPosition(position).toString()

                    Log.d("MapsFragment", "center=${mapsVM.getMyLocation()}, time(mTime)=${mType}")

                    markerList.forEachIndexed { index, marker ->
                        // Do something with each marker in the list
                        if ((mType == "Social" || mType == "Any") && eventList[index].eventType == "Social") {
                            if (distMarkerList.contains(marker)) {
                                marker.isVisible = true
                            }
                            typeMarkerList.add(marker)
                        } else if ((mType == "Business" || mType == "Any") && eventList[index].eventType == "Business") {
                            if (distMarkerList.contains(marker)) {
                                marker.isVisible = true
                            }
                            typeMarkerList.add(marker)
                        } else if ((mType == "Other" || mType == "Any") && eventList[index].eventType == "Other") {
                            if (distMarkerList.contains(marker)) {
                                marker.isVisible = true
                            }
                            typeMarkerList.add(marker)
                        } else {
                            marker.isVisible = false
                            typeMarkerList.remove(marker)
                        }
                    }
                }
            }

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

        mMap = googleMap

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapsVM.getCurrentLocation(), mapsVM.getZoom().toFloat()))

        mMap.setOnCameraIdleListener {
            // Get the center LatLng of the map when the camera stops moving
            mapsVM.setCurrentLocation(mMap.cameraPosition.target)
            mapsVM.setZoom(mMap.cameraPosition.zoom)
            Log.d("MapsFragment", mapsVM.getCurrentLocation().toString())
            // Do something with currentLatLng
        }


        Firebase.firestore.collection("posts").get().addOnSuccessListener {
            val eList = ArrayList<Post>()
            val mList = ArrayList<Marker>()
            eventList.clear()
            markerList.clear()
            distMarkerList.clear()
            typeMarkerList.clear()
            for ((index,i) in it.documents.withIndex()) {

                val post: Post = i.toObject<Post>()!!
                eList.add(post)
                //Log.d("MapsFragment", tempList[index].eventLoc)
                val loc = convertStringToLatLng(eList[index].eventLoc)

                val icon: BitmapDescriptor = if (eList[index].eventType == "Social") {
                    BitmapDescriptorFactory.fromResource(R.drawable.markertest)
                } else if (eList[index].eventType == "Business") {
                    BitmapDescriptorFactory.fromResource(R.drawable.markertestb)
                } else {
                    BitmapDescriptorFactory.fromResource(R.drawable.markertesto)
                }
                Picasso.get().load(eList[index].postUrl).fetch()
                val marker = mMap.addMarker(MarkerOptions()
                    .position(loc)
                    .title(eList[index].title)
                    //.snippet("${tempList[index].caption}@${tempList[index].postUrl}")
                    .snippet(eList[index].postId)
                    .icon(icon))!!
                mList.add(marker)
            }
            eventList.addAll(eList)
            markerList.addAll(mList)
            distMarkerList.addAll(mList)
            typeMarkerList.addAll(mList)

            val eventInfoAdapter = EventInfoAdapter(requireContext(), eventList)
            mMap.setInfoWindowAdapter(eventInfoAdapter)
            //Log.d("mapsf", postList.size.toString())
        }
        //Log.d("MapsFragment", "distance between ${postList[0].eventLoc} and ${postList[1].eventLoc} =")

        mMap.setOnMarkerClickListener { marker ->
            // Handle marker click event here
            //Toast.makeText(context, "Clicked marker: ${marker.title}", Toast.LENGTH_SHORT).show()
            Log.d("MapsFragment", "dist = ${calcDistance(mapsVM.getMyLocation(), LatLng(marker.position.latitude, marker.position.longitude))}")
            false // Return true to consume the event and prevent default behavior (such as showing info window)
        }
   }

    override fun onResume() {
        super.onResume()

        val distanceAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.distance_options))
        binding.dlist.setAdapter(distanceAdapter)

        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.type_options))
        binding.tlist.setAdapter(typeAdapter)

    }

    private fun getDistance(str: String) : Double {
        if (str == "< 1 km") {
            return 1000.0
        } else if (str == "1 – 2 km") {
            return 2000.0
        } else if (str == "2 – 5 km") {
            return 5000.0
        } else
            return 0.0
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


    private fun calcDistance(llng1: LatLng, llng2 : LatLng) : Float {
        val loc1 = Location("llng1")
        val loc2 = Location("llng2")
        loc1.latitude = llng1.latitude
        loc2.latitude = llng2.latitude
        loc1.longitude = llng1.longitude
        loc2.longitude = llng2.longitude
        return loc1.distanceTo(loc2)

    }


}