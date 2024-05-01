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
    private lateinit var binding: FragmentMessageBinding
    private lateinit var userChatAdapter: UserChatAdapter
    private var userList = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)

        setupToolbar()
        setupRecyclerView()
        loadUsers()

        return binding.root
    }

    private fun setupToolbar() {
        val backButton = binding.toolbar.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        userChatAdapter = UserChatAdapter(requireContext(), userList, arrayListOf()) { userId ->
            Log.d("MessageFragment", "Navigating to ChatRoom with userId: $userId")
            navigateToChatRoom(userId)
        }
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.messagesRecyclerView.adapter = userChatAdapter
    }

    private fun navigateToChatRoom(userId: String) {
        val bundle = Bundle().apply {
            putString("userId", userId)
        }
        findNavController().navigate(R.id.chatRoomFragment, bundle)
    }

    private fun loadUsers() {
        val currentUser = Firebase.auth.currentUser?.uid
        if (currentUser == null) {
            Toast.makeText(context, "Not logged in", Toast.LENGTH_LONG).show()
            return
        }

        val followingCollection = "$currentUser$FOLLOW"
        Firebase.firestore.collection(followingCollection).get()
            .addOnSuccessListener { documents ->
                val emails = documents.mapNotNull { it.getString("email") }
                if (emails.isNotEmpty()) {
                    fetchUsersByEmails(emails)
                } else {
                    Toast.makeText(context, "No followings found or missing emails.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to fetch following list: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("MessageFragment", "Error fetching following list", e)
            }
    }

    private fun fetchUsersByEmails(emails: List<String>) {
        Firebase.firestore.collection(USER_NODE)
            .whereIn("email", emails)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val userList = querySnapshot.documents.mapNotNull { it.toObject(User::class.java)?.apply { uid = it.id } }
                val userIds = userList.map { it.uid!! } // Now we have the actual userIds
                userChatAdapter.updateUsers(userList, userIds)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching user details: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("MessageFragment", "Error fetching user details", e)
            }
    }




}
