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
    private lateinit var binding: FragmentMyPostBinding
    private lateinit var adapter: PostAdapter
    private var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyPostBinding.inflate(inflater, container, false)
        adapter = PostAdapter(requireContext(), postList)
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        binding.rv.adapter = adapter
        loadUserPosts()

        return binding.root
    }

    private fun loadUserPosts() {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            // Assuming 'posts' is your collection where all posts are stored
            Firebase.firestore.collection("posts")
                .whereEqualTo("uid", userId)
                .get().addOnSuccessListener { documents ->
                    postList.clear()
                    for (document in documents) {
                        val post = document.toObject(Post::class.java)
                        postList.add(post)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("MyPostFragment", "Error loading user posts", e)
                }
        }
    }

    companion object {
        fun newInstance(): MyPostFragment {
            return MyPostFragment()
        }
    }
}
