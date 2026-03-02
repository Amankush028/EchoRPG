package com.echorpg.repository

import com.echorpg.data.AppDatabase
import com.echorpg.data.Girl
import com.echorpg.data.GirlEntity
import com.echorpg.data.GirlSeeder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GirlRepository(private val database: AppDatabase) {

    // FIXED: Proper seeding check
    suspend fun seedIfNeeded() {
        val unlockedGirls = database.girlDao().getAllUnlockedGirls().firstOrNull() ?: emptyList()
        if (unlockedGirls.isEmpty()) {
            GirlSeeder.seedIfNeeded(this)
        }
    }

    // NEW FUNCTION - for CharactersScreen (full unlock data)
    fun getAllUnlockedGirlsEntity(): Flow<List<GirlEntity>> {
        return database.girlDao().getAllUnlockedGirls()
    }

    // UPDATED: Now accepts name (used by seeder + future story meetings)
    suspend fun createOrMeetGirl(girlId: String, fromStory: String, revealedName: String = "???") {
        val existing = database.girlDao().getGirlById(girlId)
        if (existing == null) {
            database.girlDao().insert(
                GirlEntity(
                    id = girlId,
                    name = revealedName,
                    fromStory = fromStory,
                    appearance = "Stunning voluptuous anime beauty",
                    personality = "Seductive and mysterious",
                    kinks = "???",
                    likes = "???",
                    dislikes = "???",
                    secret = "???",
                    isUnlocked = true,
                    unlockProgress = 40,
                    relationshipLevel = 15
                )
            )
        }
    }

    // Your original functions (kept 100% intact)
    fun getAllUnlockedGirls(): Flow<List<Girl>> {
        return database.girlDao().getAllUnlockedGirls().map { list ->
            list.map { entity ->
                Girl(
                    id = entity.id.toIntOrNull() ?: 0,
                    name = entity.name,
                    fromStory = entity.fromStory,
                    status = entity.status,
                    relationshipLevel = entity.relationshipLevel
                )
            }
        }
    }

    suspend fun updateRelationship(girlId: String, level: Int) {
        database.girlDao().updateRelationship(girlId, level)
    }

    suspend fun unlockGirl(girlId: String, level: Int = 10) {
        database.girlDao().unlockGirl(girlId, level)
    }

    suspend fun unlockAttribute(girlId: String, field: String, value: String) {
        val girl = database.girlDao().getGirlById(girlId) ?: return
        val updated = when (field) {
            "name" -> girl.copy(name = value)
            "appearance" -> girl.copy(appearance = value)
            "kinks" -> girl.copy(kinks = value)
            "likes" -> girl.copy(likes = value)
            "dislikes" -> girl.copy(dislikes = value)
            "secret" -> girl.copy(secret = value)
            else -> girl
        }
        database.girlDao().insert(updated)
    }
}