package com.example.hifive.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.hifive.Models.Post
import com.example.hifive.Models.User

import com.example.hifive.R
import com.example.hifive.adapters.FollowAdapter
import com.example.hifive.adapters.PostAdapter
import com.example.hifive.databinding.FragmentHomeBinding
import com.example.hifive.utils.FOLLOW
import com.example.hifive.utils.POST
import com.example.hifive.utils.USER_NODE
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var postList = ArrayList<Post>()
    private lateinit var adapter: PostAdapter
    private var followList = ArrayList<User>()
    private lateinit var followAdapter: FollowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = PostAdapter(requireContext(), postList)
        binding.postRv.layoutManager = LinearLayoutManager(requireContext())
        binding.postRv.adapter = adapter

        followAdapter = FollowAdapter(requireContext(), followList)
        binding.followRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.followRv.adapter = followAdapter
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar2)

        loadFollows()
        loadPosts()
        loadProfileImage()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadProfileImage()
    }

    private fun loadProfileImage() {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            Firebase.firestore.collection(USER_NODE).document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<User>()
                    if (user != null && !user.image.isNullOrEmpty()) {
                        Picasso.get().load(user.image).placeholder(R.drawable.user).into(binding.imageView3)
                    } else {
                        binding.imageView3.setImageResource(R.drawable.user)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HomeFragment", "Error loading profile image", e)
                }
        }
    }

    private fun loadPosts() {
        Firebase.firestore.collection("posts").get()
            .addOnSuccessListener { documents ->
                val tempList = ArrayList<Post>()
                postList.clear()
                for (document in documents) {
                    document.toObject<Post>()?.let {
                        tempList.add(it)
                    }
                }
                postList.addAll(tempList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error loading posts: ", exception)
            }
    }

    private fun loadFollows() {
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW).get()
            .addOnSuccessListener { documents ->
                val tempList = ArrayList<User>()
                followList.clear()
                for (document in documents) {
                    document.toObject<User>()?.let {
                        tempList.add(it)
                    }
                }
                followList.addAll(tempList)
                followAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error loading follows: ", exception)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
