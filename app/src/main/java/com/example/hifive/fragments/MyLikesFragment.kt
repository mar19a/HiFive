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
    private var seenPostIds = HashSet<String>()

    override fun onResume() {
        super.onResume()
        loadLikedPosts()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
            Firebase.firestore.collection("userLikes").document(userId).collection("likes")
                .get().addOnSuccessListener { documents ->
                    val postIds = documents.map { it.id }
                    fetchPostsByIds(postIds)
                }
                .addOnFailureListener { e ->
                    Log.e("MyLikesFragment", "Error loading liked posts", e)
                }
        } else {
            postList.clear()
            adapter.notifyDataSetChanged()
            seenPostIds.clear()
        }
    }

    private fun fetchPostsByIds(postIds: List<String>) {
        postList.clear()
        seenPostIds.clear()
        postIds.forEach { postId ->
            if (!seenPostIds.contains(postId)) {
                Firebase.firestore.collection("posts").document(postId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val post = documentSnapshot.toObject(Post::class.java)
                        post?.let {
                            if (seenPostIds.add(postId)) {  // Add to seen set and check if it was truly added
                                postList.add(it)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
            }
        }
    }

    companion object {
        fun newInstance(): MyLikesFragment {
            return MyLikesFragment()
        }
    }
}
