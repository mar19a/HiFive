package com.example.hifive

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng


class MapsViewModel: ViewModel() {

    private var myLoc = LatLng(0.0, 0.0)
    private var currentLoc = LatLng(0.0, 0.0)
    private var zoom = 15f

    fun setMyLocation(loc: LatLng) {
        myLoc = loc
    }

    fun getMyLocation(): LatLng {
        return myLoc
    }

    fun setCurrentLocation(loc: LatLng) {
        currentLoc = loc
    }

    fun getCurrentLocation(): LatLng {
        return currentLoc
    }

    fun setZoom(zm: Float) {
        zoom = zm
    }

    fun getZoom() : Float {
        return zoom
    }


}