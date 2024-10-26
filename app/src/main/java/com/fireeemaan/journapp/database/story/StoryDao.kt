package com.fireeemaan.journapp.database.story

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: List<StoryEntity>)

    @Query("SELECT * FROM story")
    suspend fun getAllStories(): List<StoryEntity>

    @Query("SELECT * FROM story WHERE id = :id")
    suspend fun getStoryById(id: Int): StoryEntity

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}