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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.example.hifive.R
import android.widget.ImageView

class ChatRoomFragment : Fragment() {
    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        setupToolbar()
        return binding.root
    }

    private fun setupToolbar() {
        val backButton = binding.toolbarChatRoom.findViewById<ImageView>(R.id.backButtonChatRoom)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendMessageButton.setOnClickListener {
            val messageText = binding.messageInputEditText.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessageToFirebase(messageText)
                binding.messageInputEditText.setText("")
            }
        }
    }

    private fun sendMessageToFirebase(messageText: String) {
        val messageData = hashMapOf(
            "fromUserId" to FirebaseAuth.getInstance().currentUser?.uid,
            "messageText" to messageText,
            "timestamp" to FieldValue.serverTimestamp()
        )
        FirebaseFirestore.getInstance().collection("chatMessages").add(messageData)
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
