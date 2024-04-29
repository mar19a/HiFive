package com.example.hifive.Models

import com.google.firebase.Timestamp

data class Message(
    var messageId: String = "",
    var fromUserId: String = "",
    var toUserId: String = "",
    var messageText: String = "",
    var timestamp: Timestamp? = null,
    var senderName: String = "",
    var senderProfileImageUrl: String = ""
)
