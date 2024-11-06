package com.fireeemaan.journapp.ui.story

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.repository.StoryRepository
import com.fireeemaan.journapp.di.Injection
import com.fireeemaan.journapp.ui.story.add.AddStoryViewModel
import com.fireeemaan.journapp.ui.story.detail.DetailStoryViewModel
import com.fireeemaan.journapp.ui.story.list.ListStoryViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoryViewModelFactory private constructor(
    private val storyRepository: StoryRepository,
    private val authPref: TokenDataStore,
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ListStoryViewModel::class.java) -> {
                ListStoryViewModel(storyRepository, authPref) as T
            }

            modelClass.isAssignableFrom(DetailStoryViewModel::class.java) -> {
                DetailStoryViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(authPref) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: StoryViewModelFactory? = null
        fun getInstance(context: Context, authPref: TokenDataStore): StoryViewModelFactory =
            instance ?: synchronized(this) {
                val token = runBlocking { authPref.getAuthToken().first() }
                instance ?: StoryViewModelFactory(
                    Injection.provideStoryRepository(context, token),
                    authPref
                )
            }
    }
}