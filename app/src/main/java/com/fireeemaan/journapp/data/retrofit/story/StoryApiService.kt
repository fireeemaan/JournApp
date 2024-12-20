package com.fireeemaan.journapp.data.retrofit.story

import com.fireeemaan.journapp.data.response.AddStoryResponse
import com.fireeemaan.journapp.data.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface StoryApiService {
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<StoriesResponse>

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int = 1
    ): Response<StoriesResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Response<AddStoryResponse>
}