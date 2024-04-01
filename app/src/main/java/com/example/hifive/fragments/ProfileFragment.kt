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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.editProfile.setOnClickListener {
            val intent=Intent(activity,SignUpActivity::class.java)
            intent.putExtra("MODE",1)
            activity?.startActivity(intent)
            activity?.finish()
        }
        viewPagerAdapter=ViewPagerAdapter(requireActivity().supportFragmentManager)
        viewPagerAdapter.addFragments(MyPostFragment(),"My Post")
        viewPagerAdapter.addFragments(MyReelsFragment(),"My Reels")
        binding.viewPager.adapter=viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        return binding.root
    }

    companion object {

    }

    override fun onStart() {
        super.onStart()
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
        Firebase.firestore.collection(USER_NODE).document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                if (user != null) {
                    binding.name.text = user.name ?: ""
                    binding.bio.text = user.email ?: ""
                    if (!user.image.isNullOrEmpty()) {
                        Picasso.get().load(user.image).into(binding.profileImage)
                    }
                } else {
                    // User data not found
                    handleUserDataNotFound()
                }
            }
    } else{
            handleUserNotLoggedIn()
    }
    }

    private fun handleUserDataNotFound() {
        // Option 1: Show a toast message
        Toast.makeText(
            context,
            "User data not found. Please complete your profile.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun handleUserNotLoggedIn() {
        // Show a toast message as immediate feedback.
        Toast.makeText(context, "You must be logged in to view this page. Redirecting to login...", Toast.LENGTH_LONG).show()

        // Redirect to LoginActivity for user to log in.
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish() // Finish the current activity so the user can't navigate back to it without logging in.
    }


    }