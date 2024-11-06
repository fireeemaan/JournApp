package com.fireeemaan.journapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.fireeemaan.journapp.data.retrofit.story.StoryApiService
import com.fireeemaan.journapp.database.story.StoryDao
import com.fireeemaan.journapp.database.story.StoryEntity
import kotlinx.coroutines.Dispatchers
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.response.AddStoryResponse
import com.fireeemaan.journapp.data.response.ErrorResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val storyApiService: StoryApiService,
    private val storyDao: StoryDao
) {

    fun getAllStories(): LiveData<Result<List<StoryEntity>>> {
        return liveData(Dispatchers.IO) {
            emit(Result.Loading)

            try {
                val response = storyApiService.getStories()
                val localData = storyDao.getAllStories()
                if (localData.isNotEmpty() && localData == response) {
                    emit(Result.Success(localData))
                } else {
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

    suspend fun postStory(
        photo: MultipartBody.Part,
        description: RequestBody
    ): Result<AddStoryResponse> {
        return try {
            val response = storyApiService.addStory(photo, description)
            if (response.isSuccessful) {
                response.body()?.let { addStoryResponse ->
                    val storiesResponse = storyApiService.getStories()
                    if (storiesResponse.isSuccessful) {
                        val stories = storiesResponse.body()?.listStory?.map {
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
                        if (stories != null) {
                            storyDao.deleteAll()
                            storyDao.insert(stories)
                        }
                    }
                    Result.Success(addStoryResponse)
                } ?: Result.Error("Failed to upload story: ${response.message()}")
            } else {
                val type = object : TypeToken<ErrorResponse>() {}.type
                val errorResponse: ErrorResponse? =
                    Gson().fromJson(response.errorBody()?.charStream(), type)
                Result.Error("Error: ${errorResponse?.message}")
            }
        } catch (e: Exception) {
            Result.Error("Unexpected Error : ${e.message}")
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