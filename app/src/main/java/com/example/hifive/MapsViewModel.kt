package com.example.hifive

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng


class MapsViewModel: ViewModel() {

//    private var location = LatLng(0.0, 0.0)
//    //private lateinit var location : LatLng

    private val _location = MutableLiveData<LatLng>()

    private val location: LiveData<LatLng>
        get() = _location


//    @SuppressLint("MissingPermission")
//    fun findLocation(fusedLocationClient: FusedLocationProviderClient) {
//
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { loc ->
//                // Got last known location. In some rare situations this can be null.
//                if (loc != null) {
//                    _location.value = LatLng(loc.latitude, loc.longitude)
//                    Log.d("MapsVM", _location.value.toString())
//                } else {
//                    Log.d("MapsVM", _location.value.toString())
//                }
//            }
//            .addOnFailureListener { e ->
//                // Handle failure to retrieve location
//                Log.e("MapsVM", "Error getting location: ${e.message}", e)
//            }
//    }

    fun setLocation(loc: LatLng) {
        _location.value = loc
    }

    fun getLocation(): LatLng? {
        return _location.value
    }



}