package com.echorpg.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [
        GirlEntity::class,
        StoryProgressEntity::class,
        ChatMessageEntity::class
    ],
    version = 3,                    // ← bumped to 3
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun girlDao(): GirlDao
    abstract fun storyProgressDao(): StoryProgressDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "echorpg_database"
                )
                    .fallbackToDestructiveMigration()   // wipes old DB safely during dev
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}