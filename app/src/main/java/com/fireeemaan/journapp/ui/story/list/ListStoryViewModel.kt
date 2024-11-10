package com.fireeemaan.journapp.ui.story.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fireeemaan.journapp.data.repository.StoryRepository
import com.fireeemaan.journapp.database.story.StoryEntity

class ListStoryViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {
    val story: LiveData<PagingData<StoryEntity>> =
        storyRepository.getAllStories().cachedIn(viewModelScope)
}