package com.example.hifive.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.hifive.Models.Post
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.hifive.Models.Reel
import com.example.hifive.adapters.MyReelAdapter
import com.example.hifive.databinding.FragmentMyReelsBinding
import com.example.hifive.utils.REEL
import com.example.hifive.adapters.PostAdapter


class MyLikesFragment : Fragment() {
    private lateinit var binding: FragmentMyReelsBinding // Late initialization of binding for the fragment's view
    private lateinit var adapter: PostAdapter // Adapter for managing display of liked posts
    private var postList = ArrayList<Post>()  // List to hold posts that the user has liked
    private var seenPostIds = HashSet<String>()  // List to hold posts that the user has liked

    override fun onResume() {
        super.onResume()
        loadLikedPosts()  // Load liked posts whenever the fragment resumes
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMyReelsBinding.inflate(inflater, container, false) // Inflate the layout for this fragment
        adapter = PostAdapter(requireContext(), postList) // Initialize the adapter with context and post list
        binding.rv.layoutManager = LinearLayoutManager(requireContext()) // Set the layout manager for RecyclerView
        binding.rv.adapter = adapter // Set the adapter for RecyclerView

        loadLikedPosts() // Load liked posts after view creation

        return binding.root // Return the root of the inflated layout
    }

    private fun loadLikedPosts() {
        val userId = Firebase.auth.currentUser?.uid  // Obtain the current user's ID
        if (userId != null) {
            // Navigate into the 'userLikes' collection then into the user-specific document and 'likes' sub-collection
            Firebase.firestore.collection("userLikes").document(userId).collection("likes")
                .get().addOnSuccessListener { documents ->
                    val postIds = documents.map { it.id }  // Extract post IDs from the documents
                    fetchPostsByIds(postIds) // Fetch posts based on these IDs
                }
                .addOnFailureListener { e ->
                    Log.e("MyLikesFragment", "Error loading liked posts", e) // Log error if the fetch fails
                }
        } else {
            postList.clear() // Clear the list if no user ID is found (e.g., not logged in)
            adapter.notifyDataSetChanged() // Notify the adapter to update the view
            seenPostIds.clear() // Clear the set of seen post IDs
        }
    }

    private fun fetchPostsByIds(postIds: List<String>) {
        postList.clear()  // Clear existing posts in the list
        seenPostIds.clear() // Clear previously seen post IDs
        postIds.forEach { postId ->
            if (!seenPostIds.contains(postId)) { // Check if the post ID has not been handled before
                Firebase.firestore.collection("posts").document(postId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val post = documentSnapshot.toObject(Post::class.java) // Convert document snapshot to a Post object
                        post?.let {
                            if (seenPostIds.add(postId)) {  // Add to seen set and check if it was truly added
                                postList.add(it) // Add the post to the list
                                adapter.notifyDataSetChanged() // Notify adapter to update the view
                            }
                        }
                    }
            }
        }
    }

    companion object {
        fun newInstance(): MyLikesFragment {
            return MyLikesFragment()   //Create a new instance of MyLikesFragment
        }
    }
}
