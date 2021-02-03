package com.wallpaper.repository

import com.wallpaper.db.MainDatabase
import com.wallpaper.models.Photo
import com.wallpaper.network.RetrofitInstance
import com.wallpaper.utill.Constant.Companion.API_KEY
import com.wallpaper.utill.Constant.Companion.PER_PAGE_RESULT
import com.wallpaper.utill.Constant.Companion.PER_PAGE_TRENDING

class MainRepository(
    val db: MainDatabase
) {

    suspend fun getTrendingPhotos(pageNumber: Int) =
        RetrofitInstance.api.getTrendingPhotos(API_KEY,pageNumber, PER_PAGE_TRENDING);

    suspend fun getPhotos(photoId: Int) =
        RetrofitInstance.api.getPhotos(photoId,API_KEY)

    suspend fun getResultByQuery(query: String, pageNumber: Int) =
        RetrofitInstance.api.getResultByQuery(API_KEY,query, pageNumber, PER_PAGE_RESULT);


    suspend fun insertPhoto(photo: Photo) = db.getPhotoDao().insert(photo)

    fun getSavedPhoto() = db.getPhotoDao().getAllSavedPhoto()

    suspend fun deletePhoto(photo: Photo) = db.getPhotoDao().deleteArticle(photo)

}