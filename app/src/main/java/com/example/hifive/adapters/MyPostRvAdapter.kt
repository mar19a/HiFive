package com.example.hifive.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hifive.Models.Post
import com.example.hifive.databinding.MyPostRvDesignBinding
import com.squareup.picasso.Picasso
import com.example.hifive.R

class MyPostRvAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<MyPostRvAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: MyPostRvDesignBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyPostRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postUrl = postList[position].postUrl

        // Check if the post URL is not null or empty before loading the image
        if (!postUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(postUrl)
                .placeholder(R.drawable.post)

                .into(holder.binding.postImage)
        } else {
            // Set a default image or handle missing URL
            holder.binding.postImage.setImageResource(R.drawable.post)
        }
    }
}
