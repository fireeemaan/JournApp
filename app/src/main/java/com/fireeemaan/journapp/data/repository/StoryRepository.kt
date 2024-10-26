package com.fireeemaan.journapp.data.repository

import com.fireeemaan.journapp.database.story.StoryDao

class StoryRepository private constructor(
    private val storyDao: StoryDao
) {
}