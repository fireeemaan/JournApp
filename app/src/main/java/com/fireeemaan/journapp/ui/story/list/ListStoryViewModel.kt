package com.fireeemaan.journapp.ui.story.list

import androidx.lifecycle.ViewModel
import com.fireeemaan.journapp.data.repository.StoryRepository

class ListStoryViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {
    fun getAllStories() = storyRepository.getAllStories()
}