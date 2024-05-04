package com.example.hifive.fragments

import android.os.Bundle
import android.util.Log
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
import com.example.hifive.Models.User
import com.google.firebase.Timestamp
import com.example.hifive.utils.USER_NODE

class ChatRoomFragment : Fragment() {
    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var layoutManager: LinearLayoutManager

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
        layoutManager = LinearLayoutManager(context)
        messageAdapter = MessageAdapter(mutableListOf())
        binding.messagesRecyclerView.adapter = messageAdapter
        binding.messagesRecyclerView.layoutManager = layoutManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toUserId = arguments?.getString("userId") ?: return
        val fromUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        Log.d("ChatRoomFragment", "Received toUserId: $toUserId")
        Log.d("ChatRoomFragment", "Current fromUserId: $fromUserId")
        // Ensuring the chat session ID is generated consistently
        val chatSessionId = getChatSessionId(fromUserId, toUserId)
        Log.d("ChatRoomFragment", "Using Chat Session ID: $chatSessionId")
        loadMessages(chatSessionId)

        binding.sendMessageButton.setOnClickListener {
            val messageText = binding.messageInputEditText.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessageToFirebase(toUserId, fromUserId, chatSessionId, messageText)

                binding.messageInputEditText.setText("")
            }
        }
    }

    private fun getChatSessionId(toUserId: String, fromUserId: String): String {
        Log.d("ChatRoomFragment", "User2 ID (toUserId): $toUserId")
        Log.d("ChatRoomFragment", "User1 ID (fromUserId): $fromUserId")
        return if (toUserId < fromUserId) "$toUserId$fromUserId" else "$fromUserId$toUserId"
    }

    private fun loadMessages(chatSessionId: String) {
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
                if (messages != null && messages.isNotEmpty()) {
                    layoutManager.scrollToPosition(messageAdapter.itemCount - 1)
                }
            }
    }

    private fun sendMessageToFirebase(toUserId: String, fromUserId: String, chatSessionId: String, messageText: String) {
        // Fetch user details from the USER_NODE collection before sending a message
        FirebaseFirestore.getInstance().collection(USER_NODE).document(fromUserId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                if (user != null) {
                    val senderName = user.name ?: "Unknown User"
                    val senderProfileImageUrl = user.image ?: ""
                    val messageId = FirebaseFirestore.getInstance().collection("chatSessions").document().id
                    val timestamp = Timestamp.now()
                    val message = Message(
                        messageId, fromUserId, toUserId, messageText, timestamp, senderName, senderProfileImageUrl
                    )
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
                } else {
                    Toast.makeText(context, "User data is incomplete. Sender set to Unknown.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching user details: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
