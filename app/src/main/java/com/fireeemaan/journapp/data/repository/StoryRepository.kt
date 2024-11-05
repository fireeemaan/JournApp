package com.fireeemaan.journapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.fireeemaan.journapp.data.retrofit.story.StoryApiService
import com.fireeemaan.journapp.database.story.StoryDao
import com.fireeemaan.journapp.database.story.StoryEntity
import kotlinx.coroutines.Dispatchers
import com.fireeemaan.journapp.data.Result

class StoryRepository private constructor(
    private val storyApiService: StoryApiService,
    private val storyDao: StoryDao
) {

    fun getAllStories(): LiveData<Result<List<StoryEntity>>> {
        return liveData(Dispatchers.IO) {
            emit(Result.Loading)

            try {
                val localData = storyDao.getAllStories()
                if (localData.isNotEmpty()) {
                    emit(Result.Success(localData))
                } else {
                    val response = storyApiService.getStories()
                    if (response.isSuccessful) {
                        val storyResponse = response.body()
                        if (storyResponse != null) {
                            val stories = storyResponse.listStory
                            val storyList = stories.map {
                                StoryEntity(
                                    it.id,
                                    it.photoUrl,
                                    it.createdAt,
                                    it.name,
                                    it.description,
                                    it.lon,
                                    it.lat
                                )
                            }
                            storyDao.deleteAll()
                            storyDao.insert(storyList)
                            emit(Result.Success(storyList))
                        } else {
                            emit(Result.Error("No data found."))
                        }
                    } else {
                        emit(Result.Error("Error requesting to server."))
                    }
                }
            } catch (e: Exception) {
                emit(Result.Error("Unexpected Error : ${e.message}"))
            }
        }
    }

    fun getStoryById(id: String): LiveData<Result<StoryEntity>> {
        return liveData(Dispatchers.IO) {
            emit(Result.Loading)

            try {
                val localData = storyDao.getStoryById(id)
                if (localData != null) {
                    emit(Result.Success(localData))
                } else {
                    emit(Result.Error("Event Not Found"))
                }
            } catch (e: Exception) {
                emit(Result.Error("Unexpected Error : ${e.message}"))
            }
        }
    }

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