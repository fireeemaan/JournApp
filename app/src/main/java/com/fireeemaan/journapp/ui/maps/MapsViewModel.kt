package com.fireeemaan.journapp.ui.maps

import androidx.lifecycle.ViewModel
import com.fireeemaan.journapp.data.repository.StoryRepository

class MapsViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()
}