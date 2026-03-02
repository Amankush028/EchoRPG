package com.echorpg.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val storyId: String,
    val isUser: Boolean,
    val senderName: String,
    val content: String,
    val isNarration: Boolean = false,
    val chapter: Int,
    val timestamp: Long = System.currentTimeMillis()
)