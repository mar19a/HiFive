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

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.editProfile.setOnClickListener {
            val intent = Intent(activity, SignUpActivity::class.java)
            intent.putExtra("MODE", 1)
            activity?.startActivity(intent)
            activity?.finish()
        }

        binding.logoutButton.setOnClickListener {
            logoutUser()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        viewPagerAdapter.addFragments(MyPostFragment(), "My Post")
        viewPagerAdapter.addFragments(MyLikesFragment(), "My Likes") // Assuming you rename or repurpose MyReelsFragment
        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun logoutUser() {
        Firebase.auth.signOut()
        val intent = Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        activity?.finish()
    }

    override fun onStart() {
        super.onStart()
        updateUserProfile()
    }

    private fun updateUserProfile() {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            Firebase.firestore.collection(USER_NODE).document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<User>()
                    if (user != null) {
                        binding.name.text = user.name ?: ""
                        binding.bio.text = user.email ?: ""
                        if (!user.image.isNullOrEmpty()) {
                            Picasso.get().load(user.image).placeholder(R.drawable.user).into(binding.profileImage)
                        } else {
                            binding.profileImage.setImageResource(R.drawable.user)
                        }
                    } else {
                        handleUserDataNotFound()
                    }
                }
        } else {
            handleUserNotLoggedIn()
        }
    }

    private fun handleUserDataNotFound() {
        Toast.makeText(context, "User data not found. Please complete your profile.", Toast.LENGTH_LONG).show()
        val intent = Intent(activity, SignUpActivity::class.java).apply {
            putExtra("MODE", 1)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        activity?.startActivity(intent) ?: Toast.makeText(context, "Error starting activity. Please try again later.", Toast.LENGTH_SHORT).show()
    }

    private fun handleUserNotLoggedIn() {
        Toast.makeText(context, "You must be logged in to view this page. Redirecting to login...", Toast.LENGTH_LONG).show()
        val intent = Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        activity?.startActivity(intent) ?: Toast.makeText(context, "Error starting activity. Please try again later.", Toast.LENGTH_SHORT).show()
    }
}
