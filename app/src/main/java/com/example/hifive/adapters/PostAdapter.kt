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

        // Load user details
        Firebase.firestore.collection(USER_NODE).document(post.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                user?.let {
                    Glide.with(context).load(user.image).placeholder(R.drawable.user)
                        .into(holder.binding.profileImage)
                    holder.binding.name.text = user.name
                } ?: run {
                    holder.binding.profileImage.setImageResource(R.drawable.user)
                    holder.binding.name.text = "Unknown"
                }
            }
            .addOnFailureListener { e ->
                Log.e("PostAdapter", "Error fetching user data", e)
                holder.binding.profileImage.setImageResource(R.drawable.user)
                holder.binding.name.text = "Unknown"
            }

        // Load post image
        Glide.with(context).load(post.postUrl).placeholder(R.drawable.loading).into(holder.binding.postImage)
        holder.binding.time.text = TimeAgo.using(post.time.toLong())
        holder.binding.csption.text = post.caption

        // Set up like button
        checkLikeStatus(post, holder)

        holder.binding.like.setOnClickListener {
            toggleLike(post, holder)
        }
    }

    private fun checkLikeStatus(post: Post, holder: MyHolder) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("posts").document(post.postId).collection("likes")
            .document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                post.isLiked = documentSnapshot.exists()
                holder.binding.like.setImageResource(if (post.isLiked) R.drawable.heart_like else R.drawable.heart)
            }
    }

    private fun updateLikeButton(holder: MyHolder, post: Post) {
        holder.binding.like.setImageResource(if (post.isLiked) R.drawable.heart_like else R.drawable.heart)
    }

    private fun toggleLike(post: Post, holder: MyHolder) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val postRef = Firebase.firestore.collection("posts").document(post.postId)
        val likeRef = postRef.collection("likes").document(userId)

        Firebase.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(likeRef)
            if (snapshot.exists()) {
                transaction.delete(likeRef)
                post.isLiked = false
            } else {
                transaction.set(likeRef, hashMapOf("timestamp" to System.currentTimeMillis()))
                post.isLiked = true
            }
        }.addOnSuccessListener {
            Log.d("PostAdapter", "Like status toggled.")
            holder.binding.like.setImageResource(if (post.isLiked) R.drawable.heart_like else R.drawable.heart)
        }.addOnFailureListener {
            Log.e("PostAdapter", "Failed to toggle like status.", it)
        }
    }

}

