package com.example.hifive.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hifive.Models.User
import com.example.hifive.R
import com.example.hifive.databinding.ItemUserBinding
// Adapter for managing chat user lists in a RecyclerView.
class UserChatAdapter(
    private val context: Context,
    private var userList: MutableList<User>,
    private var userIds: MutableList<String>,
    private val onUserClick: (String) -> Unit
) : RecyclerView.Adapter<UserChatAdapter.UserViewHolder>() {

    // Updates the list of users and their IDs in the adapter.
    fun updateUsers(newUsers: List<User>, newUserIds: List<String>) {
        if (newUsers.size != newUserIds.size) {
            throw IllegalArgumentException("Users and User IDs lists must have the same size")
        }
        userList.clear()
        userIds.clear()
        userList.addAll(newUsers)
        userIds.addAll(newUserIds)
        notifyDataSetChanged()
    }
    // Inner class to manage view holding operations.
    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // Sets an OnClickListener to handle user clicks, executing a passed function.
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onUserClick(userIds[position])
                }
            }
        }
    }
    // Creates and returns a ViewHolder for each user in the list.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(binding)
    }
    // Binds user data to each ViewHolder.
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        Glide.with(context).load(user.image).placeholder(R.drawable.user).into(holder.binding.userImageView)
        holder.binding.userNameText.text = user.name
    }
    // Returns the total number of users in the list.
    override fun getItemCount(): Int = userList.size
}
