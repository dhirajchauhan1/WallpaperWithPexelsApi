package com.hdwallpaper4K.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hdwallpaper4K.R
import com.hdwallpaper4K.databinding.ActivityMainBinding
import com.hdwallpaper4K.db.MainDatabase
import com.hdwallpaper4K.repository.MainRepository

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var viewModel:MainViewModel;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = MainRepository(MainDatabase(this))
        val viewModelProviderFactory = ViewModelProviderFactory( application,repository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(MainViewModel::class.java)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)
    }
}