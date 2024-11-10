package com.fireeemaan.journapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.fireeemaan.journapp.data.retrofit.story.StoryApiService
import com.fireeemaan.journapp.database.story.StoryDao
import com.fireeemaan.journapp.database.story.StoryEntity
import kotlinx.coroutines.Dispatchers
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.remotemediator.StoryRemoteMediator
import com.fireeemaan.journapp.data.response.AddStoryResponse
import com.fireeemaan.journapp.data.response.ErrorResponse
import com.fireeemaan.journapp.database.story.StoryDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val storyApiService: StoryApiService,
    private val storyDao: StoryDao,
    private val storyDatabase: StoryDatabase
) {

    fun getAllStories(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 2
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, storyApiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
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

    fun getStoriesWithLocation(): LiveData<Result<List<StoryEntity>>> {
        return liveData(Dispatchers.IO) {
            try {
                val response = storyApiService.getStoriesWithLocation()
                if (!response.isSuccessful) {
                    emit(Result.Error("Failed to get stories data."))
                } else {
                    val stories = response.body()?.listStory?.map {
                        StoryEntity(
                            id = it.id,
                            photoUrl = it.photoUrl,
                            createdAt = it.createdAt,
                            name = it.name,
                            description = it.description,
                            lat = it.lat,
                            lon = it.lon
                        )
                    }
                    if (stories != null) {
                        emit(Result.Success(stories))
                    }
                }
            } catch (e: Exception) {
                emit(Result.Error("Unexpected Error: $e"))
            }
        }
    }

    suspend fun postStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null

    ): Result<AddStoryResponse> {
        return try {
            val response = storyApiService.addStory(photo, description, lat, lon)

            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                val type = object : TypeToken<ErrorResponse>() {}.type
                val errorResponse: ErrorResponse? = try {
                    Gson().fromJson(response.errorBody()?.charStream(), type)
                } catch (e: Exception) {
                    null
                }
                Result.Error("Error: ${errorResponse?.message}")
            }
            
        } catch (e: Exception) {
            Log.e("StoryRepository", "postStoryCatch: ${e.message}")
            Result.Error("Unexpected Error : ${e.message}")
        }
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyApiService: StoryApiService,
            storyDao: StoryDao,
            storyDatabase: StoryDatabase
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(storyApiService, storyDao, storyDatabase)
        }.also { instance = it }
    }
}