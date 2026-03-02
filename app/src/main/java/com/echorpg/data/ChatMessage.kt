package com.echorpg.data

sealed class ChatMessage {
    data class User(
        val text: String,
        val timestamp: Long = System.currentTimeMillis(),
        val chapter: Int = 1
    ) : ChatMessage()

    data class Ai(
        val text: String,
        val name: String = "Lira",
        val timestamp: Long = System.currentTimeMillis(),
        val chapter: Int = 1
    ) : ChatMessage()

    data class Narration(
        val text: String,
        val timestamp: Long = System.currentTimeMillis(),
        val chapter: Int = 1
    ) : ChatMessage()
}