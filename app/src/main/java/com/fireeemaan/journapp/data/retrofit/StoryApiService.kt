package com.fireeemaan.journapp.data.retrofit

import androidx.room.Query
import com.fireeemaan.journapp.data.response.DetailStoryResponse
import com.fireeemaan.journapp.data.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface StoryApiService {
    @GET("stories")
    suspend fun getStories(): Response<StoriesResponse>

    @GET("stories/{id}")
    suspend fun getStoryById(
        @Path("id") id: String
    ): Response<DetailStoryResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    )
}