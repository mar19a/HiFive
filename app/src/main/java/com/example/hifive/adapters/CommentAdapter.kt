package com.example.hifive.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hifive.Models.Post
import com.example.hifive.R
import com.example.hifive.databinding.CommentItemBinding
import com.github.marlonlom.utilities.timeago.TimeAgo

// The CommentAdapter is responsible for displaying a list of comments in a RecyclerView.
class CommentAdapter(private var comments: List<Post.Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }
    // onBindViewHolder binds data to the ViewHolder, setting up the content that will be displayed.
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }
    // getItemCount returns the size of the list that contains the items we want to display.
    override fun getItemCount(): Int = comments.size
    // updateComments allows updating the list of comments displayed by the RecyclerView.
    fun updateComments(newComments: List<Post.Comment>) {
        comments = newComments
        notifyDataSetChanged()
    }
    // ViewHolder class provides a reference to the views for each data item
    inner class CommentViewHolder(private val binding: CommentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Post.Comment) {
            binding.userName.text = comment.userName ?: "Unknown"
            Glide.with(binding.userImage.context)
                .load(comment.userImageUrl)
                .placeholder(R.drawable.user)
                .into(binding.userImage)
            binding.commentText.text = comment.text
            // Setting up a human-readable time since the comment was posted.
            comment.timestamp?.let {
                val timeAgo = TimeAgo.using(it)
                binding.timeAgo.text = timeAgo
            }
        }
    }

}
