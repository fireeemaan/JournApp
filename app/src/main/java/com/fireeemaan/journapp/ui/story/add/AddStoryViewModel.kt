package com.fireeemaan.journapp.ui.story.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.repository.StoryRepository
import com.fireeemaan.journapp.data.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _addStoryResponse = MutableLiveData<Result<AddStoryResponse>>()
    val addStoryResponse: LiveData<Result<AddStoryResponse>> get() = _addStoryResponse

    fun addStory(
        photoUrl: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) {
        viewModelScope.launch {
            _addStoryResponse.postValue(Result.Loading)
            val result = storyRepository.postStory(photoUrl, description, lat, lon)
            _addStoryResponse.postValue(result)
        }
    }
}