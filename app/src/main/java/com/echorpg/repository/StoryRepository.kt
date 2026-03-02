package com.echorpg.repository

import com.echorpg.data.*
import kotlinx.coroutines.flow.Flow

class StoryRepository(private val database: AppDatabase) {
    private val progressDao = database.storyProgressDao()
    private val messageDao = database.chatMessageDao()

    // Progress
    suspend fun saveProgress(progress: StoryProgressEntity) = progressDao.insertOrUpdate(progress)
    fun getProgress(storyId: String): Flow<StoryProgressEntity?> = progressDao.getProgress(storyId)
    fun getAllProgress(): Flow<List<StoryProgressEntity>> = progressDao.getAllProgress()
    suspend fun deleteProgress(storyId: String) {
        progressDao.getProgress(storyId).collect { it?.let { progressDao.delete(it) } }
    }

    // Messages
    suspend fun saveMessage(message: ChatMessageEntity) = messageDao.insert(message)
    fun getMessages(storyId: String): Flow<List<ChatMessageEntity>> = messageDao.getMessagesForStory(storyId)
    suspend fun deleteMessages(storyId: String) = messageDao.deleteAllForStory(storyId)
}