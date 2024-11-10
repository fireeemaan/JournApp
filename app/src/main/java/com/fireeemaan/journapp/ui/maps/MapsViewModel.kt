package com.fireeemaan.journapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.repository.StoryRepository
import com.fireeemaan.journapp.database.story.StoryEntity

class MapsViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()
}