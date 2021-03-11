package com.hdwallpaper4K.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.google.gson.Gson
import com.hdwallpaper4K.models.ModelCategory
import com.hdwallpaper4K.models.ModelColorCat
import com.hdwallpaper4K.models.Photo
import com.hdwallpaper4K.models.Trending
import com.hdwallpaper4K.repository.MainRepository
import com.hdwallpaper4K.utill.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException


class MainViewModel(
        application:Application,
    val repository: MainRepository
) : AndroidViewModel(application) {

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
        safeTrendingPhotoCall()
    }
    private suspend fun safeTrendingPhotoCall(){
        trendingPhoto.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = repository.getTrendingPhotos(trendingPhotoPage)
                trendingPhoto.postValue(handleTrendingResponse(response))
            }
            else{
                trendingPhoto.postValue(Resource.Error("No Internet Connection"))
            }
        }
        catch (t: Throwable){
            when(t){
                is IOException -> trendingPhoto.postValue(Resource.Error("Network Api Failure"))
                else  -> trendingPhoto.postValue(Resource.Error("Conversion Error"))
            }
        }
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
       safeGetResultByQuery(query)
    }
    private suspend fun safeGetResultByQuery(query: String){
        resultPhoto.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = repository.getResultByQuery(query,resultPhotoPage)
                resultPhoto.postValue(handleResultByQueryResponse(response))
            }
            else{
                resultPhoto.postValue(Resource.Error("No Internet Connection"))
            }
        }
        catch (t: Throwable){
            when(t){
                is IOException -> resultPhoto.postValue(Resource.Error("Network Api Failure"))
                else  -> resultPhoto.postValue(Resource.Error("Conversion Error"))
            }
        }
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

    private fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(
                Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE-> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}