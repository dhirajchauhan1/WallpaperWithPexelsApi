package com.hdwallpaper4K.network

import com.hdwallpaper4K.models.Photo
import com.hdwallpaper4K.models.Trending
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("curated")
    suspend fun getTrendingPhotos(
        @Header("Authorization") auth:String,
        @Query("page") page:Int,
        @Query("per_page") per_page:Int
    ) : Response<Trending>

    @GET("search")
    suspend fun getResultByQuery(
        @Header("Authorization") auth:String,
        @Query("query") query:String,
        @Query("page") page:Int,
        @Query("per_page") per_page:Int
    ) : Response<Trending>

    @GET("photos/{id}")
    suspend fun getPhotos(
        @Path("id") id: Int,
        @Header("Authorization") auth:String
    ) : Response<Photo>
}