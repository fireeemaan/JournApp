package com.fireeemaan.journapp.database.story

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: List<StoryEntity>)

    @Query("SELECT * FROM story")
    fun getAllStories(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM story WHERE id = :id")
    suspend fun getStoryById(id: String): StoryEntity?

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}