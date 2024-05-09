package com.example.hifive.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
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
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
// HomeFragment manages the display of posts and follow .
class HomeFragment : Fragment() {
    // Lateinit used for properties that are guaranteed to be initialized before use.
    private lateinit var binding: FragmentHomeBinding
    private var postList = ArrayList<Post>() // List to store posts.
    private lateinit var adapter: PostAdapter // Adapter for RecyclerView to display posts.
    private var followList = ArrayList<User>() // List to store followings.
    private lateinit var followAdapter: FollowAdapter // Adapter for RecyclerView to display followings.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Inflates the layout for the fragment, and initializes RecyclerViews.
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
        // Set the toolbar as the app bar for the activity.
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar2)

        loadFollows() // Load followings.
        loadPosts() // Load posts.
        loadProfileImage() // Load profile image.

        return binding.root
    }

    // Reloads the profile image when the fragment resumes.
    override fun onResume() {
        super.onResume()
        loadProfileImage()
    }

    // Loads the user's profile image from Firebase Firestore.
    private fun loadProfileImage() {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            Firebase.firestore.collection(USER_NODE).document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<User>()
                    if (user != null && !user.image.isNullOrEmpty()) {
                        // Use Picasso to load the image into the ImageView.
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
    // Fetches and sorts posts from Firestore, displaying them in descending order by time.
    private fun loadPosts() {
        Firebase.firestore.collection("posts")
            .orderBy("time", Query.Direction.DESCENDING)// Sorts the documents by 'time' in descending order.
            .get()
            .addOnSuccessListener { documents ->
                val tempList = ArrayList<Post>()
                postList.clear()
                for (document in documents) {
                    document.toObject<Post>()?.let {
                        tempList.add(it)
                    }
                }
                postList.addAll(tempList)
                adapter.notifyDataSetChanged() // Notify the adapter that data has changed.
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error loading posts: ", exception)
            }
    }

    // Loads the followings for the user.
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
                followAdapter.notifyDataSetChanged() // Notify the adapter that data has changed.
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error loading follows: ", exception)
            }
    }
    // Inflates the options menu.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    // Handles item selections in the options menu.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.options -> {
                navigateToMessages() // Navigate to the messages screen.
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Navigates to the messages screen using NavController.
    private fun navigateToMessages() {
        findNavController().navigate(R.id.action_homeFragment_to_messageFragment)
    }


    companion object {
        // Factory method to create a new instance of this fragment.
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
