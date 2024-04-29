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
    private var userList: MutableList<User>,
    private var userIds: MutableList<String>,
    private val onUserClick: (String) -> Unit
) : RecyclerView.Adapter<UserChatAdapter.UserViewHolder>() {

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

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onUserClick(userIds[position])
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
