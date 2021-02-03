package com.wallpaper.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.wallpaper.R
import com.wallpaper.adapters.ResultAdapter
import com.wallpaper.databinding.FragmentCategoryBinding
import com.wallpaper.databinding.FragmentFavouriteBinding
import com.wallpaper.ui.DetailActivity
import com.wallpaper.ui.MainActivity
import com.wallpaper.ui.MainViewModel

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    lateinit var resultAdapter: ResultAdapter
    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        setUpRecyclerView()
        viewModel = (activity as MainActivity).viewModel

        showProgressBar()
        viewModel.getSavedPhoto().observe(viewLifecycleOwner, Observer { photoList ->
            resultAdapter.differ.submitList(photoList)
            hideProgressBar()
        })

        return binding.root
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvNoResult.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.tvNoResult.visibility = View.INVISIBLE
    }

    private fun setUpRecyclerView(){
        resultAdapter = ResultAdapter(activity?.applicationContext!!)
        resultAdapter.setHasStableIds(true);
        binding.recyclerFav.apply {
            adapter = resultAdapter
        }

        resultAdapter.onItemClick = { photoList, position ->
            val jsonPhoto:String = Gson().toJson(photoList)
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("jsonPhoto", jsonPhoto)
            intent.putExtra("position", position)
            startActivity(intent)
        }
    }
}