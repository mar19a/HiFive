package com.example.hifive.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hifive.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.hifive.Models.User
import com.example.hifive.R
import com.example.hifive.SignUpActivity
import com.example.hifive.adapters.ViewPagerAdapter
import com.example.hifive.databinding.FragmentProfileBinding
import com.example.hifive.utils.USER_NODE
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    // Variables for view binding and adapter declaration
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Standard override of onCreate method in fragments
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment using binding
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        // Set up the edit profile button which starts SignUpActivity for editing profile
        binding.editProfile.setOnClickListener {
            val intent = Intent(activity, SignUpActivity::class.java)
            intent.putExtra("MODE", 1)  // Pass "MODE" as extra to specify edit mode
            activity?.startActivity(intent)
            activity?.finish() // Finish the current activity
        }
        // Set up the logout button which performs user sign out
        binding.logoutButton.setOnClickListener {
            logoutUser() // Calls the logoutUser method defined below
        }

        return binding.root // Return the root view from the binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager() // Set up the view pager when the view is created
    }

    private fun setupViewPager() {
        // Initialize the ViewPager adapter
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        // Add MyPostFragment and MyLikesFragment to the ViewPager
        viewPagerAdapter.addFragments(MyPostFragment(), getString(R.string.my_post))
        viewPagerAdapter.addFragments(MyLikesFragment(), getString(R.string.my_likes))
        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager) // Link tab layout with view pager
    }

    private fun logoutUser() {
        // Perform sign out with Firebase Auth
        Firebase.auth.signOut()
        // Intent to go back to the LoginActivity
        val intent = Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        }
        startActivity(intent)
        activity?.finish() // Finish the current activity
    }

    override fun onStart() {
        super.onStart()
        updateUserProfile() // Update user profile information on start
    }

    private fun updateUserProfile() {
        val userId = Firebase.auth.currentUser?.uid // Retrieve current user's ID from Firebase
        if (userId != null) {
            // Fetch the user document from Firestore
            Firebase.firestore.collection(USER_NODE).document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<User>() // Fetch the user document from Firestore
                    if (user != null) {
                        // Set user name and bio from the fetched user object
                        binding.name.text = user.name ?: ""
                        binding.bio.text = user.email ?: ""
                        // Load user profile image using Picasso
                        if (!user.image.isNullOrEmpty()) {
                            Picasso.get().load(user.image).placeholder(R.drawable.user).into(binding.profileImage)
                        } else {
                            binding.profileImage.setImageResource(R.drawable.user) // Set default image if null
                        }
                    } else {
                        handleUserDataNotFound() // Handle case where user data is not found
                    }
                }
        } else {
            handleUserNotLoggedIn()  // Handle case where user is not logged in
        }
    }

    private fun handleUserDataNotFound() {
        // Display a toast message for missing user data
        Toast.makeText(context,
            getString(R.string.user_data_not_found_please_complete_your_profile), Toast.LENGTH_LONG).show()
        // Redirect to SignUpActivity to complete profile
        val intent = Intent(activity, SignUpActivity::class.java).apply {
            putExtra("MODE", 1)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        activity?.startActivity(intent) ?: Toast.makeText(context,
            getString(R.string.error_starting_activity_please_try_again_later), Toast.LENGTH_SHORT).show()
    }

    private fun handleUserNotLoggedIn() {
        // Display a toast for not logged in error
        Toast.makeText(context, getString(R.string.not_logged_in_error), Toast.LENGTH_LONG).show()
        // Redirect to LoginActivity
        val intent = Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        activity?.startActivity(intent) ?: Toast.makeText(context, getString(R.string.error_starting_activity_please_try_again_later), Toast.LENGTH_SHORT).show()
    }
}
