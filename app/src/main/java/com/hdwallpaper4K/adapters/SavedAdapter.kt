package com.hdwallpaper4K.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.hdwallpaper4K.R
import com.hdwallpaper4K.databinding.ItemSavedBinding
import com.hdwallpaper4K.models.Photo

class SavedAdapter(val context: Context): RecyclerView.Adapter<SavedAdapter.ViewHolder>(){

    var onItemClick: ((List<Photo>, Int) -> Unit)? = null
    var onDeleteClick: ((List<Photo>, Int) -> Unit)? = null

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
                R.layout.item_saved,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo: Photo = differ.currentList[position]
        holder.binding.apply {
            Glide.with(context).load(photo.src.medium)
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(object : SimpleTarget<Drawable?>() {

                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                        layout.background = resource
                    }
                })
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = ItemSavedBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList, position)
            }

            binding.deleteSaved.setOnClickListener {
                onDeleteClick?.invoke(differ.currentList, position)
            }
        }
    }

}