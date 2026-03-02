package com.echorpg.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_progress")
data class StoryProgressEntity(
    @PrimaryKey val storyId: String,          // e.g. "fantasy_kingdom"
    val title: String,
    val personaName: String,
    val currentChapter: Int = 1,
    val progress: Float = 0f,                 // 0.0 - 1.0
    val totalChapters: Int = 10,
    val lastUpdated: Long = System.currentTimeMillis()
)