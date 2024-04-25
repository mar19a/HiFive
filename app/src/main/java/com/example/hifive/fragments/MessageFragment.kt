package com.example.hifive.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hifive.Models.User
import com.example.hifive.R
import com.example.hifive.adapters.UserChatAdapter
import com.example.hifive.databinding.FragmentMessageBinding
import com.example.hifive.utils.USER_NODE
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MessageFragment : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    private lateinit var userChatAdapter: UserChatAdapter
    private var userList = ArrayList<User>()
    private var userIdList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)

        setupToolbar()
        setupRecyclerView()
        loadUsers()

        return binding.root
    }

    private fun setupToolbar() {
        val backButton = binding.toolbar.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            findNavController().navigateUp()  // Navigate up in the navigation stack
        }
    }

    private fun setupRecyclerView() {
        userChatAdapter = UserChatAdapter(requireContext(), userList, userIdList) { userId ->
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
        Firebase.firestore.collection(USER_NODE).get()
            .addOnSuccessListener { result ->
                userList.clear()
                userIdList.clear()
                result.documents.forEach { document ->
                    document.toObject<User>()?.let { user ->
                        userList.add(user)
                        userIdList.add(document.id)
                    }
                }
                userChatAdapter.notifyDataSetChanged()
            }
    }
}
