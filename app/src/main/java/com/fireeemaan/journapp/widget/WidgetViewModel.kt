package com.fireeemaan.journapp.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireeemaan.journapp.data.retrofit.story.StoryApiConfig
import com.fireeemaan.journapp.database.story.StoryEntity
import kotlinx.coroutines.launch

class WidgetViewModel : ViewModel() {
    suspend fun getStories(token: String): List<StoryEntity>? {
        return try {
            val response = StoryApiConfig.getApiService(token).getStories()
            if (response.isSuccessful) {
                response.body()?.listStory?.map { listStoryItem ->
                    StoryEntity(
                        id = listStoryItem.id,
                        name = listStoryItem.name,
                        description = listStoryItem.description,
                        photoUrl = listStoryItem.photoUrl,
                        createdAt = listStoryItem.createdAt
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}