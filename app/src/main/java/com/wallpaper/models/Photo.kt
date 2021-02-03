package com.wallpaper.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_photos"
)
data class Photo(
    val avg_color: String,
    val height: Int,
    @PrimaryKey
    val id: Int,
    val liked: Boolean,
    val photographer: String,
    val photographer_id: Int,
    val photographer_url: String,
    val src: Src,
    val url: String,
    val width: Int
)