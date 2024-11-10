package com.fireeemaan.journapp.widget

import androidx.lifecycle.ViewModel
import com.fireeemaan.journapp.data.retrofit.story.StoryApiConfig
import com.fireeemaan.journapp.database.story.StoryEntity

class WidgetViewModel : ViewModel() {

    private var page: Int = 1
    private var size: Int = 5

    suspend fun getStories(token: String): List<StoryEntity>? {
        return try {
            val response = StoryApiConfig.getApiService(token).getStories(page, size)
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