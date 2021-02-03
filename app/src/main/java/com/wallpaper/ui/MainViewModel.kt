package com.wallpaper.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.google.gson.Gson
import com.wallpaper.models.ModelCategory
import com.wallpaper.models.ModelColorCat
import com.wallpaper.models.Photo
import com.wallpaper.models.Trending
import com.wallpaper.repository.MainRepository
import com.wallpaper.utill.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class MainViewModel(
    val repository: MainRepository
) : ViewModel() {

    val trendingPhoto: MutableLiveData<Resource<Trending>> = MutableLiveData()
    var trendingPhotoPage = 1
    var trendingPhotoResponse: Trending? = null

    val resultPhoto: MutableLiveData<Resource<Trending>> = MutableLiveData()
    var resultPhotoPage = 1
    var resultPhotoResponse: Trending? = null

    val photo:MutableLiveData<Resource<Photo>> = MutableLiveData()
    val colorCatList: MutableLiveData<List<ModelColorCat>> = MutableLiveData()
    val categoryList: MutableLiveData<List<ModelCategory>> = MutableLiveData()

    init {
        getCategory()
        getColors()
        getTrendingPhoto()
    }


    fun getColors() {
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var mRef: DatabaseReference = database.getReference("colors")

        // Read from the database
        mRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val colorList: MutableList<ModelColorCat> = mutableListOf()
                for (ds in dataSnapshot.children) {
                    var modelColorCat: ModelColorCat =
                        ds.getValue(ModelColorCat::class.java)!!
                    colorList.add(modelColorCat)
                    Log.w("MainViewodel", modelColorCat.name)
                }

                colorCatList.postValue(colorList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("MainViewModel", "Failed to read value.", error.toException())
            }
        })

    }

    fun getCategory() {
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var mRef: DatabaseReference = database.getReference("category")

        // Read from the database
        mRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val catList: MutableList<ModelCategory> = mutableListOf()
                for (ds in dataSnapshot.children) {
                    var modelCat:ModelCategory =
                        ds.getValue(ModelCategory::class.java)!!
                    catList.add(modelCat)
                }

                categoryList.postValue(catList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("MainViewModel", "Failed to read value.", error.toException())
            }
        })

    }

    fun getTrendingPhoto() = viewModelScope.launch {
        trendingPhoto.postValue(Resource.Loading())
        val response = repository.getTrendingPhotos(trendingPhotoPage)
        trendingPhoto.postValue(handleTrendingResponse(response))
    }
    private fun handleTrendingResponse(response: Response<Trending>) : Resource<Trending>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                trendingPhotoPage += 1
                if (trendingPhotoResponse == null){
                    trendingPhotoResponse = resultResponse
                }
                else{
                    val oldPhoto = trendingPhotoResponse?.photos
                    val newPhoto = resultResponse.photos
                    oldPhoto?.addAll(newPhoto)
                }

                return Resource.Success(trendingPhotoResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getPhotos(photoId: Int) = viewModelScope.launch {
        photo.postValue(Resource.Loading())
        val response = repository.getPhotos(photoId)
       // Log.d("response", Gson().toJson(response)+"  " +response.message())
        photo.postValue(handlePhotosResponse(response))
    }
    private fun handlePhotosResponse(response: Response<Photo>) : Resource<Photo>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message(), response.body())
    }

    fun getResultByQuery(query: String) = viewModelScope.launch {
        resultPhoto.postValue(Resource.Loading())
        val response = repository.getResultByQuery(query,resultPhotoPage)
        // Log.d("response", Gson().toJson(response)+"  " +response.message())
        resultPhoto.postValue(handleResultByQueryResponse(response))
    }
    private fun handleResultByQueryResponse(response: Response<Trending>) : Resource<Trending>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                resultPhotoPage += 1
                if (resultPhotoResponse == null){
                    resultPhotoResponse = resultResponse
                }
                else{
                    val oldPhoto = resultPhotoResponse?.photos
                    val newPhoto = resultResponse.photos
                    oldPhoto?.addAll(newPhoto)
                }

                return Resource.Success(resultPhotoResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    fun savePhoto(photo: Photo) = viewModelScope.launch {
        repository.insertPhoto(photo)
        Log.e("save", Gson().toJson(photo)+"hwvd")
    }

    fun getSavedPhoto() = repository.getSavedPhoto()

    fun deletePhoto(photo: Photo) = viewModelScope.launch {
        repository.deletePhoto(photo)
    }

}