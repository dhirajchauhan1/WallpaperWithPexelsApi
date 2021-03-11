package com.hdwallpaper4K.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hdwallpaper4K.R
import com.hdwallpaper4K.databinding.ItemCategoryColorBinding
import com.hdwallpaper4K.models.ModelColorCat

class ColorCatAdapter() : RecyclerView.Adapter<ColorCatAdapter.ViewHolder>() {

    var onItemClick: ((ModelColorCat) -> Unit)? = null

    private val differCallBack = object : DiffUtil.ItemCallback<ModelColorCat>(){
        override fun areItemsTheSame(oldItem:ModelColorCat, newItem: ModelColorCat): Boolean {
            return oldItem.color == newItem.color
        }

        override fun areContentsTheSame(oldItem:ModelColorCat, newItem: ModelColorCat): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category_color, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var modelColorCat:ModelColorCat = differ.currentList[position]

        with(holder) {
           binding.cardView.setCardBackgroundColor(Color.parseColor(modelColorCat.color.toString()))
        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = ItemCategoryColorBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList[position])
            }
        }
    }
}