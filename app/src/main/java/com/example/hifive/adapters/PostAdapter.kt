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
import java.text.SimpleDateFormat
import java.util.Locale
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager


class PostAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.MyHolder>() {

    inner class MyHolder(var binding: PostRvBinding) : RecyclerView.ViewHolder(binding.root) {
        var commentAdapter: CommentAdapter? = null

        init {
            commentAdapter = CommentAdapter(listOf())
            binding.commentsRecyclerView.adapter = commentAdapter
            binding.commentsRecyclerView.layoutManager = LinearLayoutManager(context)

            binding.eventAddress.setOnClickListener {
                val address = postList[adapterPosition].eventAddr
                openGoogleMaps(address)
            }
            binding.commentToggle.setOnClickListener {
                if (binding.commentsSection.visibility == View.VISIBLE) {
                    binding.commentsSection.visibility = View.GONE
                } else {
                    binding.commentsSection.visibility = View.VISIBLE
                    loadComments(postList[adapterPosition].postId)
                }
            }
            binding.submitCommentButton.setOnClickListener {
                val commentText = binding.commentInput.text.toString()
                if (commentText.isNotEmpty()) {
                    submitComment(commentText, postList[adapterPosition].postId)
                    binding.commentInput.setText("")
                }
            }
        }


        fun loadComments(postId: String) {
            Firebase.firestore.collection("posts").document(postId)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Toast.makeText(context, "Error loading comments: ${e.message}", Toast.LENGTH_LONG).show()
                        return@addSnapshotListener
                    }
                    val comments = snapshot?.documents?.mapNotNull { it.toObject(Post.Comment::class.java) }
                    commentAdapter?.updateComments(comments ?: listOf())
                }
        }

    }

    private fun submitComment(commentText: String, postId: String) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            // Fetch user details when submitting a comment
            Firebase.firestore.collection(USER_NODE).document(currentUser.uid).get()
                .addOnSuccessListener { userSnapshot ->
                    val user = userSnapshot.toObject<User>()
                    if (user != null) {
                        val comment = hashMapOf(
                            "userId" to currentUser.uid,
                            "userName" to user.name,
                            "userImageUrl" to user.image,
                            "text" to commentText,
                            "timestamp" to System.currentTimeMillis()
                        )
                        Firebase.firestore.collection("posts").document(postId)
                            .collection("comments").add(comment)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Comment added successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to add comment: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(context, "User data not available.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "You need to be logged in to comment.", Toast.LENGTH_LONG).show()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = PostRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(binding)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        val post = postList[position]
        holder.commentAdapter?.updateComments(listOf())
        holder.binding.eventAddress.text = post.eventAddr

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
        holder.binding.caption.text = post.caption
        holder.binding.time.text = TimeAgo.using(post.time.toLong())

        // Format and display date and time together
        formatDateTime(holder, post.eventDate, post.eventTime)

        holder.binding.eventAddress.text = post.eventAddr
        holder.binding.eventAddress.setOnClickListener {
            openGoogleMapsForDirections(post.eventLoc)
        }

        holder.binding.imageView8.setOnClickListener {
            openGoogleMapsForDirections(post.eventLoc)
        }

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

    private fun openGoogleMaps(address: String) {
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun formatDateTime(holder: MyHolder, date: String, time: String) {

            holder.binding.eventDate.text = "$date $time"
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
