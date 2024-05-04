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
            Toast.makeText(context, getString(R.string.not_logged_in), Toast.LENGTH_LONG).show()
            return
        }

        val followingCollection = "$currentUser$FOLLOW"
        Firebase.firestore.collection(followingCollection)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(context,
                        getString(R.string.error_fetching_following_list, e.message), Toast.LENGTH_LONG).show()
                    Log.e("MessageFragment", "Error fetching following list", e)
                    return@addSnapshotListener
                }

                val emails = snapshot?.documents?.mapNotNull { it.getString("email") }
                if (emails != null && emails.isNotEmpty()) {
                    fetchUsersByEmails(emails)
                } else {
                    userChatAdapter.updateUsers(emptyList(), emptyList()) // Clear the list if no following found
                    Toast.makeText(context,
                        getString(R.string.no_followings_found_or_missing_emails), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchUsersByEmails(emails: List<String>) {
        Firebase.firestore.collection(USER_NODE)
            .whereIn("email", emails)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Error fetching user details: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("MessageFragment", "Error fetching user details", e)
                    return@addSnapshotListener
                }

                val userList = querySnapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.apply { uid = doc.id }
                } ?: listOf()

                val userIds = userList.mapNotNull { it.uid }
                userChatAdapter.updateUsers(userList, userIds)
                Log.d("MessageFragment", "Loaded users and IDs: $userIds")
            }
    }




}
