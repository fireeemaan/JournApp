package com.fireeemaan.journapp

import com.fireeemaan.journapp.database.story.StoryEntity

object DataDummy {
    fun generateDummyStoryEntity(): List<StoryEntity> {
        val storyList: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..0) {
            val story = StoryEntity(
                "story-fvfvf4f4",
                "upload.wikimedia.org/wikipedia/commons/thumb/9/90/National_emblem_of_Indonesia_Garuda_Pancasila.svg/768px-National_emblem_of_Indonesia_Garuda_Pancasila.svg.png",
                "2024-11-10T07:10:49.808Z",
                "Asep Presto",
                "Garuda Pancasila",
                40.3399,
                127.5101
            )
            storyList.add(story)
        }
        return storyList
    }
}