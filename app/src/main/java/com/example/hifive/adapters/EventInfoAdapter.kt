package com.example.hifive.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.example.hifive.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback

class EventInfoAdapter(var context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        // Return null here if you only want the default info window behavior
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        // Inflate your custom layout for the info window
        val view = LayoutInflater.from(context).inflate(R.layout.event_info, null)

        // Find views from your custom layout
        val imageView = view.findViewById<ImageView>(R.id.image)
        val textView = view.findViewById<TextView>(R.id.title)
        Log.d("EventInfoAdapter", marker.snippet.toString())
        //imageView.setImageResource()
        textView.text = marker.title
        //var url = "https://firebasestorage.googleapis.com/v0/b/hifive-f43ec.appspot.com/o/PostImages%2F5cb76fa4-3ef6-4f6d-aeac-8cc048ea7fb0?alt=media&token=7170c4fc-6c2e-43e9-bbfb-a5b921ce8ea4"
        //Glide.with(context).load(getImageUrl(marker.snippet).placeholder(R.drawable.loading).into(imageView)
        Picasso.get()
            .load(getImageUrl(marker.snippet))
            .placeholder(R.drawable.loading)
            .into(imageView)
        // Return the custom view for the info window
        return view
    }

    private fun getImageUrl(str: String?) : String {
        var imageUrl = ""
        val lastIndex = str?.lastIndexOf('@')
        if (str != null) {
            if (lastIndex != null) {
                if (lastIndex != -1 && lastIndex < str.length - 1) {
                    // Extract the substring after the last '=' character
                    imageUrl = str.substring(lastIndex + 1)
                    // Now imageUrl contains the URL part of the snippet
                } else {
                    //imageUrl = default
                }
            }
        }
        Log.d("EventInfoAdapter", imageUrl)
        return imageUrl
    }

}
