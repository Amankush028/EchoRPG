package com.echorpg.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: StoryProgressEntity)

    @Query("SELECT * FROM story_progress WHERE storyId = :storyId")
    fun getProgress(storyId: String): Flow<StoryProgressEntity?>

    @Query("SELECT * FROM story_progress")
    fun getAllProgress(): Flow<List<StoryProgressEntity>>

    @Delete
    suspend fun delete(progress: StoryProgressEntity)
}