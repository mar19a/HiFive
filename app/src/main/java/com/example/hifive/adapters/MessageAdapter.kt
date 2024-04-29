package com.example.hifive.adapters

import com.example.hifive.Models.Message
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hifive.R
import com.example.hifive.databinding.ItemMessageBinding
import com.google.firebase.Timestamp

class MessageAdapter(private val messages: MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size
    fun updateMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.messageTextView.text = message.messageText
            binding.senderNameTextView.text = message.senderName
            binding.timestampTextView.text = formatDate(message.timestamp)
            Glide.with(binding.profileImageView.context)
                .load(message.senderProfileImageUrl)
                .placeholder(R.drawable.user)
                .into(binding.profileImageView)
        }

        private fun formatDate(timestamp: Timestamp?): String {
            return timestamp?.toDate()?.toString() ?: ""
        }
    }
}
