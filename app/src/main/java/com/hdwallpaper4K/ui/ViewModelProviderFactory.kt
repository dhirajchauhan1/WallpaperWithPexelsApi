package com.hdwallpaper4K.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hdwallpaper4K.repository.MainRepository

class ViewModelProviderFactory(
        val application: Application,
        val repository: MainRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(application, repository) as T
    }
}