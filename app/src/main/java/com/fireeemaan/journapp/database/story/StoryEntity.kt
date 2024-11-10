package com.fireeemaan.journapp.database.story

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class StoryEntity(
    @ColumnInfo("id")
    @PrimaryKey
    val id: String,

    @ColumnInfo("photoUrl")
    val photoUrl: String,

    @ColumnInfo("createdAt")
    val createdAt: String,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("description")
    val description: String,

    @ColumnInfo("lat")
    val lat: Double? = null,

    @ColumnInfo("lon")
    val lon: Double? = null,

    )
