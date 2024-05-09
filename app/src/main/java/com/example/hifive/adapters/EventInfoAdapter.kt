package com.example.hifive.adapters

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import com.example.hifive.R
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.hifive.Models.Post
import com.example.hifive.databinding.EventInfoBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class EventInfoAdapter(var context: Context, private var eventList: ArrayList<Post>) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        // Return null here if you only want the default info window behavior
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        // Inflate your custom layout for the info window
        val binding = EventInfoBinding.inflate(LayoutInflater.from(context))

        val event = eventList.find { it.postId == marker.snippet }

        if (event != null) {
            // Post with the specified id found
            binding.title.text = event.title
            Picasso.get()
                .load(event.postUrl)
                .placeholder(R.drawable.loading)
                .into(binding.image)
            binding.caption.text = event.caption
            binding.address.text = event.eventAddr
            binding.date.text = event.eventDate
            binding.time.text = convert24To12(event.eventTime)
            Log.d("EventInfoAdapter","Post found: ${event.caption}")
        } else {
            // Post with the specified id not found
            Log.d("EventInfoAdapter","Post with id ${marker.snippet} not found")
        }

        return binding.root
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

    private fun convert24To12(time24: String): String {
        val parts = time24.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1]

        val suffix = if (hour < 12) "AM" else "PM"
        val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

        return String.format("%02d:%s %s", hour12, minute, suffix)
    }

}
