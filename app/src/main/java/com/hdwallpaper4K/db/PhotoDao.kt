package com.hdwallpaper4K.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hdwallpaper4K.models.Photo


@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo): Long

    @Query("SELECT * FROM saved_photos")
    fun getAllSavedPhoto(): LiveData<List<Photo>>

    @Delete
    suspend fun deleteArticle(photo: Photo)
}