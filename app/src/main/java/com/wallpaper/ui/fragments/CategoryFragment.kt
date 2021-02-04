package com.wallpaper.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.gson.Gson
import com.wallpaper.adapters.CategoryAdapter
import com.wallpaper.adapters.ColorCatAdapter
import com.wallpaper.adapters.TrendingPhotoAdapter
import com.wallpaper.databinding.FragmentCategoryBinding
import com.wallpaper.models.ModelColorCat
import com.wallpaper.ui.DetailActivity
import com.wallpaper.ui.MainActivity
import com.wallpaper.ui.MainViewModel
import com.wallpaper.ui.ResultActivity
import com.wallpaper.utill.Constant
import com.wallpaper.utill.Constant.Companion.PAGE_LIMIT_FOR_TRENDING
import com.wallpaper.utill.Constant.Companion.PER_PAGE_RESULT
import com.wallpaper.utill.Constant.Companion.PER_PAGE_TRENDING
import com.wallpaper.utill.Resource
import es.dmoral.toasty.Toasty


class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: MainViewModel
    lateinit var trendingPhotoAdapter: TrendingPhotoAdapter
    lateinit var colorCatAdapter: ColorCatAdapter
    lateinit var categoryAdapter: CategoryAdapter
    val TAG = "CatFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        setUpRecyclerView()

        viewModel = (activity as MainActivity).viewModel
        viewModel.trendingPhoto.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { photoResponse ->
                        trendingPhotoAdapter.differ.submitList(photoResponse.photos.toList())
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toasty.error(activity as MainActivity, "An Error Occured : $message", Toasty.LENGTH_LONG).show()
                        Log.e(TAG, "An Error Occured : $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

            }
        })

        viewModel.colorCatList.observe(viewLifecycleOwner, Observer { colorList ->
            colorCatAdapter.differ.submitList(colorList)
        })

        viewModel.categoryList.observe(viewLifecycleOwner, Observer { category->
            categoryAdapter.differ.submitList(category)
        })

        binding.edtSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH;
        binding.btnSearch.setOnClickListener {
            if (binding.edtSearch.text.isNotEmpty()){
                val intent1 = Intent(activity, ResultActivity::class.java)
                intent1.putExtra("category_name", binding.edtSearch.text.toString())
                startActivity(intent1)
            }
        }

        return binding.root
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

            if (!recyclerView.canScrollHorizontally(1)) {
                Log.d(TAG, "Last")

                val isLastPage = viewModel.trendingPhotoPage == PAGE_LIMIT_FOR_TRENDING

                if(!isLoading && !isLastPage){
                    viewModel.getTrendingPhoto()
                    isScrolling = false

                    Log.d(TAG, viewModel.trendingPhotoPage.toString())
                }
            }

        }
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

    private fun setUpRecyclerView(){
        trendingPhotoAdapter = TrendingPhotoAdapter(activity)
        binding.recyclerTrending.apply {
            adapter = trendingPhotoAdapter
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            addOnScrollListener(this@CategoryFragment.scrollListener)
        }
        colorCatAdapter = ColorCatAdapter()
        binding.recyclerColorCat.apply {
            adapter = colorCatAdapter
        }

        categoryAdapter = CategoryAdapter(activity?.applicationContext!!)
        binding.recyclerCategory.apply {
            adapter = categoryAdapter
        }

        trendingPhotoAdapter.onItemClick = { photoList, position ->
            val jsonPhoto:String = Gson().toJson(photoList)
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("jsonPhoto", jsonPhoto)
            intent.putExtra("position", position)
            startActivity(intent)
        }

        categoryAdapter.onItemClick = { category ->
            val intent1 = Intent(activity, ResultActivity::class.java)
            intent1.putExtra("category_name", category.name)
            startActivity(intent1)
        }

        colorCatAdapter.onItemClick = { category ->
            val intent1 = Intent(activity, ResultActivity::class.java)
            intent1.putExtra("category_name", category.name)
            startActivity(intent1)
        }
    }

}