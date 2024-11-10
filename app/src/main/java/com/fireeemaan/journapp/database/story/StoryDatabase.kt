package com.fireeemaan.journapp.database.story

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fireeemaan.journapp.database.remotekeys.RemoteKeys
import com.fireeemaan.journapp.database.remotekeys.RemoteKeysDao

@Database(entities = [StoryEntity::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            if (INSTANCE == null) {
                synchronized(StoryDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        StoryDatabase::class.java,
                        "storydb"
                    ).build()
                }
            }
            return INSTANCE as StoryDatabase
        }
    }
}