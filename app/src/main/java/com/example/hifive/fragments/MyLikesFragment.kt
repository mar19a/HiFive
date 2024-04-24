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
    private lateinit var binding: FragmentMyReelsBinding
    private lateinit var adapter: PostAdapter
    private var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyReelsBinding.inflate(inflater, container, false)
        adapter = PostAdapter(requireContext(), postList)
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        binding.rv.adapter = adapter

        loadLikedPosts()

        return binding.root
    }

    private fun loadLikedPosts() {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            Firebase.firestore.collection("posts")
                .whereEqualTo("isLikedByCurrentUser", true)
                .get().addOnSuccessListener { documents ->
                    postList.clear()
                    for (document in documents) {
                        val post = document.toObject(Post::class.java)
                        postList.add(post)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("MyLikesFragment", "Error loading liked posts", e)
                }
        }
    }


    companion object {
        fun newInstance(): MyLikesFragment {
            return MyLikesFragment()
        }
    }
}
