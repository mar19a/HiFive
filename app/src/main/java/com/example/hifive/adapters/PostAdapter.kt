package com.example.hifive.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
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
import com.google.firebase.auth.ktx.auth


class PostAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.MyHolder>() {

    inner class MyHolder(var binding: PostRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = PostRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(binding)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val post = postList[position]

        Firebase.firestore.collection(USER_NODE).document(post.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                user?.let {
                    Glide.with(context).load(user.image).placeholder(R.drawable.user)
                        .into(holder.binding.profileImage)
                    holder.binding.name.text = user.name
                } ?: run {
                    holder.binding.profileImage.setImageResource(R.drawable.user)
                    holder.binding.name.text = context.getString(R.string.unknown)
                }
            }
            .addOnFailureListener { e ->
                Log.e("PostAdapter", "Error fetching user data", e)
                holder.binding.profileImage.setImageResource(R.drawable.user)
                holder.binding.name.text = context.getString(R.string.unknown)
            }

        Glide.with(context).load(post.postUrl).placeholder(R.drawable.loading).into(holder.binding.postImage)
        //TODO: Fix Localization / Change time display - (when the event will be?)
        holder.binding.time.text = TimeAgo.using(post.time.toLong())
        holder.binding.caption.text = post.caption

        checkLikeStatus(post, holder)

        holder.binding.like.setOnClickListener {
            toggleLike(post, holder)
        }

        holder.binding.share.setOnClickListener {
            var i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, postList.get(position).postUrl)
            context.startActivity(i)

        }

        holder.binding.imageView8.setOnClickListener {
            openGoogleMapsForDirections(post.eventLoc)
        }
    }

    private fun checkLikeStatus(post: Post, holder: MyHolder) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("userLikes").document(userId).collection("likes")
            .document(post.postId).get()
            .addOnSuccessListener { documentSnapshot ->
                post.isLikedByCurrentUser = documentSnapshot.exists()
                holder.binding.like.setImageResource(if (post.isLikedByCurrentUser) R.drawable.heart_like else R.drawable.heart)
            }
    }

    private fun toggleLike(post: Post, holder: MyHolder) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val likeRef = Firebase.firestore.collection("userLikes").document(userId).collection("likes").document(post.postId)

        Firebase.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(likeRef)
            if (snapshot.exists()) {
                transaction.delete(likeRef)
                post.isLikedByCurrentUser = false
            } else {
                transaction.set(likeRef, hashMapOf("timestamp" to System.currentTimeMillis()))
                post.isLikedByCurrentUser = true
            }
        }.addOnSuccessListener {
            Log.d("PostAdapter", "Like status toggled.")
            holder.binding.like.setImageResource(if (post.isLikedByCurrentUser) R.drawable.heart_like else R.drawable.heart)
        }.addOnFailureListener { e ->
            Log.e("PostAdapter", "Failed to toggle like status.", e)
        }
    }

    private fun openGoogleMapsForDirections(eventLoc: String) {
        val gmmIntentUri = Uri.parse("google.navigation:q=$eventLoc")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            Toast.makeText(context, context.getString(R.string.map_app_error), Toast.LENGTH_SHORT).show()
        }
    }
}
