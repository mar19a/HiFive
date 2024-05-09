package com.example.hifive.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.hifive.Models.Post
import com.example.hifive.adapters.MyPostRvAdapter
import com.example.hifive.databinding.FragmentMyPostBinding
import com.example.hifive.adapters.PostAdapter
import androidx.recyclerview.widget.LinearLayoutManager


class MyPostFragment : Fragment() {
    private lateinit var binding: FragmentMyPostBinding // Late initialization for fragment binding
    private lateinit var adapter: PostAdapter  // Adapter for displaying posts
    private var postList = ArrayList<Post>()  // Adapter for displaying posts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyPostBinding.inflate(inflater, container, false)
        adapter = PostAdapter(requireContext(), postList)  // Initialize the adapter with context and the post list
        binding.rv.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager for RecyclerView
        binding.rv.adapter = adapter // Set the adapter for RecyclerView
        loadUserPosts() // Load the posts belonging to the user

        return binding.root // Return the root of the inflated layout
    }

    private fun loadUserPosts() {
        val userId = Firebase.auth.currentUser?.uid // Retrieve the current user's ID
        if (userId != null) {
            // Query the 'posts' collection for posts where 'uid' matches the current user's ID
            Firebase.firestore.collection("posts")
                .whereEqualTo("uid", userId)
                .get().addOnSuccessListener { documents ->
                    postList.clear() // Clear existing posts from the list
                    for (document in documents) {
                        val post = document.toObject(Post::class.java) // Convert each document into a Post object
                        postList.add(post) // Add the post to the list
                    }
                    adapter.notifyDataSetChanged() // Notify the adapter to update the view
                }
                .addOnFailureListener { e ->
                    Log.e("MyPostFragment", "Error loading user posts", e) // Log error if the fetch fails
                }
        }
    }

    companion object {
        fun newInstance(): MyPostFragment {
            return MyPostFragment()
        }
    }
}
