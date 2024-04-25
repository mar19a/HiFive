package com.example.hifive.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hifive.Models.User
import com.example.hifive.R
import com.example.hifive.databinding.ItemUserBinding
class UserChatAdapter(
    private val context: Context,
    private val userList: List<User>,
    private val userIds: List<String>, // List of user IDs corresponding to userList
    private val onUserClick: (String) -> Unit // Expecting user ID
) : RecyclerView.Adapter<UserChatAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onUserClick(userIds[position]) // Pass the user ID instead of the user object
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        Glide.with(context).load(user.image).placeholder(R.drawable.user).into(holder.binding.userImageView)
        holder.binding.userNameText.text = user.name
    }

    override fun getItemCount(): Int = userList.size
}
