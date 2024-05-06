package com.example.hifive.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hifive.Models.Post
import com.example.hifive.databinding.CommentItemBinding

class CommentAdapter(private var comments: List<Post.Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun updateComments(newComments: List<Post.Comment>) {
        comments = newComments
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(private val binding: CommentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Post.Comment) {
            binding.userName.text = comment.userName
            Glide.with(binding.userImage.context).load(comment.userImageUrl).into(binding.userImage)
            binding.commentText.text = comment.text
        }
    }
}
