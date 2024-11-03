package com.fireeemaan.journapp.di

import android.content.Context
import com.fireeemaan.journapp.data.repository.AuthRepository
import com.fireeemaan.journapp.data.repository.StoryRepository
import com.fireeemaan.journapp.data.retrofit.auth.AuthApiConfig
import com.fireeemaan.journapp.data.retrofit.story.StoryApiConfig
import com.fireeemaan.journapp.database.story.StoryDatabase

object Injection {
    fun provideStoryRepository(context: Context, token: String): StoryRepository {
        val storyApiService = StoryApiConfig.getApiService(token)
        val database = StoryDatabase.getDatabase(context)
        val dao = database.storyDao()
        return StoryRepository.getInstance(storyApiService, dao)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        val authApiService = AuthApiConfig.getApiService()
        return AuthRepository.getInstance(authApiService)
    }
}