package com.fireeemaan.journapp.ui.story.detail

import androidx.lifecycle.ViewModel
import com.fireeemaan.journapp.data.repository.StoryRepository

class DetailStoryViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getStory(id: String) = storyRepository.getStoryById(id)
}