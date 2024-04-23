package com.example.hifive.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.hifive.Models.Post
import com.example.hifive.Models.User
import com.example.hifive.R
import com.example.hifive.databinding.PostRvBinding
import com.example.hifive.utils.USER_NODE


class PostAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.MyHolder>() {


    inner class MyHolder(var binding: PostRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        var binding = PostRvBinding.inflate(LayoutInflater.from(context), parent, false)

        return MyHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val post = postList[position]

        // Load user details and handle image securely
        Firebase.firestore.collection(USER_NODE).document(post.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                if (user != null && !user.image.isNullOrEmpty()) {
                    Glide.with(context).load(user.image).placeholder(R.drawable.user)
                        .into(holder.binding.profileImage)
                    holder.binding.name.text = user.name
                } else {
                    holder.binding.profileImage.setImageResource(R.drawable.user) // Fallback image
                    holder.binding.name.text = "Unknown"
                }
            }
            .addOnFailureListener { e ->
                Log.e("PostAdapter", "Error fetching user data", e)
                holder.binding.profileImage.setImageResource(R.drawable.user) // Fallback image on error
                holder.binding.name.text = "Unknown"
            }

        // Validate post URL before using it
        if (!post.postUrl.isNullOrEmpty()) {
            Glide.with(context).load(post.postUrl).placeholder(R.drawable.loading)
                .into(holder.binding.postImage)
        } else {
            holder.binding.postImage.setImageResource(R.drawable.loading) // Fallback image if URL is empty
        }

        // Handling other bindings and user interactions
        try {
            val text = TimeAgo.using(post.time.toLong())
            holder.binding.time.text = text
        } catch (e: Exception) {
            holder.binding.time.text = ""
            Log.e("PostAdapter", "Error formatting time", e)
        }

        holder.binding.share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, post.postUrl)
            }
            context.startActivity(intent)
        }

        holder.binding.csption.text = post.caption
        holder.binding.like.setOnClickListener {
            holder.binding.like.setImageResource(R.drawable.heart_like)
        }
    }
}