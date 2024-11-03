package com.fireeemaan.journapp.data.repository

import com.fireeemaan.journapp.data.retrofit.story.StoryApiService
import com.fireeemaan.journapp.database.story.StoryDao

class StoryRepository private constructor(
    private val storyApiService: StoryApiService,
    private val storyDao: StoryDao
) {


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyApiService: StoryApiService,
            storyDao: StoryDao
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(storyApiService, storyDao)
        }.also { instance = it }
    }
}