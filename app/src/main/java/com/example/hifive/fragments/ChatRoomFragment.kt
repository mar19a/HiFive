package com.example.hifive.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hifive.databinding.FragmentChatRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.hifive.R
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hifive.adapters.MessageAdapter
import com.example.hifive.Models.Message
import com.google.firebase.Timestamp

class ChatRoomFragment : Fragment() {
    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var layoutManager: LinearLayoutManager // Ensure layoutManager is declared and initialized

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        setupToolbar()
        setupRecyclerView()
        return binding.root
    }

    private fun setupToolbar() {
        val backButton = binding.toolbarChatRoom.findViewById<ImageView>(R.id.backButtonChatRoom)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        layoutManager = LinearLayoutManager(context) // Initialize layoutManager here
        messageAdapter = MessageAdapter(mutableListOf())
        binding.messagesRecyclerView.adapter = messageAdapter
        binding.messagesRecyclerView.layoutManager = layoutManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toUserId = arguments?.getString("userId") ?: return

        loadMessages(toUserId)

        binding.sendMessageButton.setOnClickListener {
            val messageText = binding.messageInputEditText.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessageToFirebase(toUserId, messageText)
                binding.messageInputEditText.setText("")
            }
        }
    }

    private fun loadMessages(toUserId: String) {
        val fromUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val chatSessionId = if (fromUserId < toUserId) "$fromUserId$toUserId" else "$toUserId$fromUserId"

        FirebaseFirestore.getInstance().collection("chatSessions")
            .document(chatSessionId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Error loading messages: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) }
                messageAdapter.updateMessages(messages ?: listOf())
                layoutManager.scrollToPosition(messageAdapter.itemCount - 1)
            }
    }

    private fun sendMessageToFirebase(toUserId: String, messageText: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val fromUserId = currentUser?.uid
        if (fromUserId == null) {
            Toast.makeText(context, "Error: User not logged in", Toast.LENGTH_LONG).show()
            return
        }

        val messageId = FirebaseFirestore.getInstance().collection("chatSessions").document().id
        val timestamp = Timestamp.now() // Use Firebase Timestamp here
        val message = Message(
            messageId,
            fromUserId,
            toUserId,
            messageText,
            timestamp
        )

        val chatSessionId = if (fromUserId < toUserId) "$fromUserId$toUserId" else "$toUserId$fromUserId"
        FirebaseFirestore.getInstance().collection("chatSessions")
            .document(chatSessionId)
            .collection("messages")
            .document(messageId)
            .set(message)
            .addOnSuccessListener {
                Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to send message: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}