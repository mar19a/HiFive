package com.example.hifive.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.hifive.Models.User
import com.example.hifive.R
import com.example.hifive.databinding.SearchRvBinding
import com.example.hifive.utils.FOLLOW
// Adapter class for displaying user search results in a RecyclerView.
class SearchAdapter(var context: Context, var userList: ArrayList<User>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: SearchRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = SearchRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }
    // Returns the size of the list that contains the user data.
    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var isfollow=false
        // Loads user image into the ImageView, with a default placeholder if needed.
        Glide.with(context).load(userList.get(position).image).placeholder(R.drawable.user)
            .into(holder.binding.profileImage)
        // Sets the user's name into the TextView.
        holder.binding.name.text = userList.get(position).name
        // Checks if the current user is following this user.
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOW)
            .whereEqualTo("email", userList.get(position).email).get().addOnSuccessListener {

                if (it.documents.size==0){
                        isfollow=false
                }else{
                    holder.binding.follow.text= context.getString(R.string.unfollow)
                    isfollow=true
                }

        }
        // Sets an OnClickListener to handle follow/unfollow functionality.
        holder.binding.follow.setOnClickListener {
            if (isfollow){
                // Sets an OnClickListener to handle follow/unfollow functionality.
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOW)
                    .whereEqualTo("email", userList.get(position).email).get().addOnSuccessListener {

                       Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOW).document(it.documents.get(0).id).delete()
                        holder.binding.follow.text= context.getString(R.string.follow)
                        isfollow=false

                    }
            }else{
                // If not following, follow the user on click.
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW).document()
                    .set(userList.get(position))
                holder.binding.follow.text = context.getString(R.string.unfollow)
                isfollow=true
            }


        }
    }
}