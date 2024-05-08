package com.example.hifive.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.hifive.Models.Post
import com.example.hifive.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class EventInfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private var postList = ArrayList<Post>()


    override fun getInfoWindow(marker: Marker): View? {
        // Return null here if you only want the default info window behavior
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        // Inflate your custom layout for the info window
        val view = LayoutInflater.from(context).inflate(R.layout.event_info, null)

        // Find views from your custom layout
        val imageView = view.findViewById<ImageView>(R.id.image)
        val textView = view.findViewById<TextView>(R.id.text)
        Log.d("EventInfoAdapter", marker.snippet.toString())
        //imageView.setImageResource()
        textView.text = marker.title
        Glide.with(context)
            .load(marker.snippet?.let { getImageUrl(it) })
            .placeholder(R.drawable.user) // Optional placeholder image while loading
            .into(imageView)

        // Return the custom view for the info window
        return view
    }

    private fun getImageUrl(str: String) : String {
        var imageUrl = ""
        val lastIndex = str.lastIndexOf('@')
        if (lastIndex != -1 && lastIndex < str.length - 1) {
            // Extract the substring after the last '=' character
            imageUrl = str.substring(lastIndex + 1)
            // Now imageUrl contains the URL part of the snippet
        } else {
            //imageUrl = default
        }
        Log.d("EventInfoAdapter", imageUrl)
        return imageUrl
    }
}
