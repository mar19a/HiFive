package com.example.hifive.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hifive.Models.User
import com.example.hifive.R
import com.example.hifive.adapters.UserChatAdapter
import com.example.hifive.databinding.FragmentMessageBinding
import com.example.hifive.utils.USER_NODE
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.example.hifive.utils.FOLLOW
import com.google.firebase.auth.ktx.auth
import android.util.Log


class MessageFragment : Fragment() {
    private lateinit var binding: FragmentMessageBinding // Late initialization for binding
    private lateinit var userChatAdapter: UserChatAdapter // Adapter for chat user RecyclerView
    private var userList = ArrayList<User>() // List to store chat users
    // Inflate the layout and setup the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false) // Inflate layout for this fragment

        setupToolbar() // Setup the toolbar with back navigation
        setupRecyclerView() // Setup RecyclerView for chat users
        loadUsers() // Load chat users

        return binding.root // Return the layout root
    }

    // Setup toolbar with back button functionality
    private fun setupToolbar() {
        val backButton = binding.toolbar.findViewById<ImageView>(R.id.backButton) // Access back button from toolbar
        backButton.setOnClickListener {
            findNavController().navigateUp() // Navigate back when back button is clicked
        }
    }
    // Setup RecyclerView with layout manager and adapter
    private fun setupRecyclerView() {
        userChatAdapter = UserChatAdapter(requireContext(), userList, arrayListOf()) { userId ->
            Log.d("MessageFragment", "Navigating to ChatRoom with userId: $userId") // Log navigation event
            navigateToChatRoom(userId) // Navigate to chat room with specific user ID
        }
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager for RecyclerView
        binding.messagesRecyclerView.adapter = userChatAdapter // Set adapter for RecyclerView
    }

    // Navigate to chat room passing user ID
    private fun navigateToChatRoom(userId: String) {
        val bundle = Bundle().apply {
            putString("userId", userId) // Put user ID into bundle
        }
        findNavController().navigate(R.id.chatRoomFragment, bundle) // Navigate to chat room fragment with bundle
    }

    // Load chat users from Firebase Firestore
    private fun loadUsers() {
        val currentUser = Firebase.auth.currentUser?.uid // Get current user ID
        if (currentUser == null) {
            Toast.makeText(context, getString(R.string.not_logged_in), Toast.LENGTH_LONG).show() // Show not logged in message
            return
        }

        val followingCollection = "$currentUser$FOLLOW" // Define the following collection path
        Firebase.firestore.collection(followingCollection)
            .addSnapshotListener { snapshot, e ->  // Listen to real-time updates in following collection
                if (e != null) {
                    Toast.makeText(context,
                        getString(R.string.error_fetching_following_list, e.message), Toast.LENGTH_LONG).show()  // Show error message on fetch failure
                    Log.e("MessageFragment", "Error fetching following list", e)
                    return@addSnapshotListener
                }

                val emails = snapshot?.documents?.mapNotNull { it.getString("email") } // Extract emails from snapshot documents
                if (emails != null && emails.isNotEmpty()) {
                    fetchUsersByEmails(emails) // Fetch users by emails if available
                } else {
                    userChatAdapter.updateUsers(emptyList(), emptyList()) // Clear the list if no following found
                    Toast.makeText(context,
                        getString(R.string.no_followings_found_or_missing_emails), Toast.LENGTH_SHORT).show()  // Show no followings found message
                }
            }
    }

    // Fetch users details by emails from Firebase Firestore
    private fun fetchUsersByEmails(emails: List<String>) {
        Firebase.firestore.collection(USER_NODE)
            .whereIn("email", emails)// Query users by email
            .addSnapshotListener { querySnapshot, e -> // Listen to real-time updates for user details
                if (e != null) {
                    Toast.makeText(context, "Error fetching user details: ${e.message}", Toast.LENGTH_LONG).show() // Show error message on failure
                    Log.e("MessageFragment", "Error fetching user details", e)
                    return@addSnapshotListener
                }

                val userList = querySnapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.apply { uid = doc.id } // Map documents to User objects and set their UIDs
                } ?: listOf()

                val userIds = userList.mapNotNull { it.uid } // Extract user IDs from user list
                userChatAdapter.updateUsers(userList, userIds) // Update adapter with new user list and IDs
                Log.d("MessageFragment", "Loaded users and IDs: $userIds") // Log loaded users and IDs
            }
    }




}
