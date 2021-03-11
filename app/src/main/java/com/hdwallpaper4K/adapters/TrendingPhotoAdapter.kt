package com.hdwallpaper4K.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hdwallpaper4K.R
import com.hdwallpaper4K.databinding.ItemTrendingBinding
import com.hdwallpaper4K.models.Photo

class TrendingPhotoAdapter(val context: FragmentActivity?): RecyclerView.Adapter<TrendingPhotoAdapter.ViewHolder>(){

    var onItemClick: ((List<Photo>, Int) -> Unit)? = null

    private val differCallBack = object : DiffUtil.ItemCallback<Photo>(){
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_trending,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo:Photo = differ.currentList[position]
        holder.binding.apply {
            Glide.with(context?.applicationContext!!).load(photo.src.medium)
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
            /*Picasso.get().load(photo.src.original).into(imageView);*/
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = ItemTrendingBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList, position)
            }
        }
    }
}