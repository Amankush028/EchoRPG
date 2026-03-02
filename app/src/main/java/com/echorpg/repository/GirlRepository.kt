package com.echorpg.repository

import com.echorpg.data.AppDatabase
import com.echorpg.data.Girl
import com.echorpg.data.GirlEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GirlRepository(private val database: AppDatabase) {

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

    suspend fun createOrMeetGirl(girlId: String, fromStory: String) {
        val existing = database.girlDao().getGirlById(girlId)
        if (existing == null) {
            database.girlDao().insert(
                GirlEntity(
                    id = girlId,
                    name = "???",
                    fromStory = fromStory,
                    appearance = "A mysterious silhouette",
                    personality = "???",
                    kinks = "???",
                    likes = "???",
                    dislikes = "???",
                    secret = "???"
                )
            )
        }
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

    suspend fun updateRelationship(girlId: String, level: Int) {
        database.girlDao().updateRelationship(girlId, level)
    }
}