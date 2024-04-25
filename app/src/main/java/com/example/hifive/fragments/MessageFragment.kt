package com.example.hifive.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hifive.R
import com.example.hifive.databinding.FragmentMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MessageFragment : Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()  // Uses NavController to navigate up in the navigation stack
        }

        binding.sendButton.setOnClickListener {
            sendMessage(binding.messageInput.text.toString())
            binding.messageInput.setText("")
        }
    }


    private fun sendMessage(messageText: String) {
        val message = hashMapOf(
            "fromUserId" to FirebaseAuth.getInstance().currentUser?.uid,
            "messageText" to messageText,
            "timestamp" to FieldValue.serverTimestamp()
        )
        FirebaseFirestore.getInstance().collection("messages").add(message)
            .addOnSuccessListener { Log.d("MessageFragment", "Message sent successfully") }
            .addOnFailureListener { e -> Log.e("MessageFragment", "Error sending message", e) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}