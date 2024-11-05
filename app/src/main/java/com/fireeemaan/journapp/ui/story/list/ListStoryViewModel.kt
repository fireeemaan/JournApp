package com.fireeemaan.journapp.ui.story.list

import androidx.lifecycle.ViewModel
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.repository.StoryRepository

class ListStoryViewModel(
    private val storyRepository: StoryRepository,
    private val authPref: TokenDataStore
) : ViewModel() {
    fun getAllStores() = storyRepository.getAllStories()
}