package com.echorpg.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "girls")
data class GirlEntity(
    @PrimaryKey val id: String,                    // e.g. "lira_fantasy_001"
    val name: String = "???",                      // locked until revealed
    val fromStory: String,
    val status: String = "Unknown",

    // Dynamic fields (start locked)
    val appearance: String = "???",                // "Silhouette of a mysterious woman"
    val personality: String = "???",
    val kinks: String = "???",                     // comma separated
    val likes: String = "???",
    val dislikes: String = "???",
    val secret: String = "???",

    val relationshipLevel: Int = 0,
    val isUnlocked: Boolean = false,               // basic unlock
    val unlockProgress: Int = 0,                   // 0-100% details revealed
    val unlockedAt: Long = System.currentTimeMillis()
)