package com.echorpg.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insert(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE storyId = :storyId ORDER BY timestamp ASC")
    fun getMessagesForStory(storyId: String): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages WHERE storyId = :storyId")
    suspend fun deleteAllForStory(storyId: String)
}