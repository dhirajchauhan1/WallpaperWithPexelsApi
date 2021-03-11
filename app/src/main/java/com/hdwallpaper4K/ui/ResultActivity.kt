package com.hdwallpaper4K.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hdwallpaper4K.adapters.ResultAdapter
import com.hdwallpaper4K.databinding.ActivityResultBinding
import com.hdwallpaper4K.db.MainDatabase
import com.hdwallpaper4K.repository.MainRepository
import com.hdwallpaper4K.utill.Constant.Companion.PAGE_LIMIT_FOR_RESULT
import com.hdwallpaper4K.utill.Resource
import es.dmoral.toasty.Toasty


class ResultActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityResultBinding
    lateinit var resultAdapter: ResultAdapter
    lateinit var categoryName:String

    val TAG = "Result"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener(View.OnClickListener {
            finish()
        })
        setUpRecyclerView()
        val repository = MainRepository(MainDatabase(this))
        val viewModelProviderFactory = ViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)

        val intent: Intent = this.intent
        categoryName= intent.getStringExtra("category_name").toString()
        binding.catName.text = categoryName?.toUpperCase()
        viewModel.getResultByQuery(categoryName.toString())

        viewModel.resultPhoto.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        if (resultResponse.total_results == 0) {
                            binding.tvNoResult.visibility = View.VISIBLE
                            Log.e("ResultActivity", "${resultResponse.total_results}")
                        } else {
                            binding.tvNoResult.visibility = View.INVISIBLE
                            resultAdapter.differ.submitList(resultResponse.photos.toList())

                        }

                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toasty.error(this, "An Error Occured : $message", Toasty.LENGTH_LONG).show()
                        Log.e("ResultActivity", "An Error Occured : $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

            }
        })
    }

    private fun showProgressBar() {
        isLoading = true
        binding.progressBar.visibility = View.VISIBLE
        Log.d(TAG, "Loading")
    }

    private fun hideProgressBar() {
        isLoading = false
        binding.progressBar.visibility = View.INVISIBLE
    }



    var isLoading = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
                Log.d(TAG, "Scrolling")

            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (!recyclerView.canScrollVertically(1)) {
                Log.d(TAG, "Last")

                val isLastPage = viewModel.resultPhotoPage == PAGE_LIMIT_FOR_RESULT
                Log.d(TAG, isLoading.toString() + isScrolling)

                if(!isLoading && !isLastPage){
                    viewModel.getResultByQuery(categoryName)
                    isScrolling = false

                    Log.d(TAG, viewModel.resultPhotoPage.toString())
                }
            }

        }
    }



    private fun setUpRecyclerView(){
        resultAdapter = ResultAdapter(this)
        binding.recyclerResult.apply {
            adapter = resultAdapter

            layoutManager = GridLayoutManager(this@ResultActivity, 3)
            addOnScrollListener(this@ResultActivity.scrollListener)


            /*binding.recyclerResult.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1)) {
                        Log.d(TAG, "Last")

                        Handler().postDelayed({
                            viewModel.getResultByQuery(categoryName)
                        }, 1000)
                    }
                }
            })*/
        }

        resultAdapter.onItemClick = { photoList, position ->

             try {
                 val jsonPhoto:String = Gson().toJson(photoList)
                 val intent = Intent(this, DetailActivity::class.java)
                 intent.putExtra("jsonPhoto", jsonPhoto)
                 intent.putExtra("position", position)
                 startActivity(intent)
             }
             catch (e: Exception){
                 Log.e(TAG, e.localizedMessage)
             }


        }

    }
}