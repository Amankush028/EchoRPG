package com.echorpg.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GirlDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(girl: GirlEntity)

    @Query("SELECT * FROM girls WHERE isUnlocked = 1")
    fun getAllUnlockedGirls(): Flow<List<GirlEntity>>

    @Query("SELECT * FROM girls WHERE fromStory = :storyTitle AND isUnlocked = 1")
    fun getGirlsFromStory(storyTitle: String): Flow<List<GirlEntity>>

    @Query("UPDATE girls SET relationshipLevel = :level WHERE id = :girlId")
    suspend fun updateRelationship(girlId: String, level: Int)

    @Query("UPDATE girls SET isUnlocked = 1, relationshipLevel = :level WHERE id = :girlId")
    suspend fun unlockGirl(girlId: String, level: Int = 10)

    @Query("SELECT * FROM girls WHERE id = :girlId")
    suspend fun getGirlById(girlId: String): GirlEntity?

    @Update
    suspend fun updateGirl(girl: GirlEntity)
}