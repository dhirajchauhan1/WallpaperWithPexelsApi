package com.wallpaper.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
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
import com.wallpaper.R
import com.wallpaper.databinding.ItemCategoryBinding
import com.wallpaper.models.ModelCategory
import com.wallpaper.models.Photo


class CategoryAdapter(val context: Context) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var onItemClick: ((ModelCategory) -> Unit)? = null

    private val differCallBack = object : DiffUtil.ItemCallback<ModelCategory>(){
        override fun areItemsTheSame(oldItem: ModelCategory, newItem: ModelCategory): Boolean {
            return oldItem.image == newItem.image
        }

        override fun areContentsTheSame(oldItem: ModelCategory, newItem: ModelCategory): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_category,
                        parent,
                        false
                )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var modelCat:ModelCategory = differ.currentList[position]
        with(holder) {
            binding.tv.text = modelCat.name

            Glide.with(context).load(modelCat.image)
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(object : SimpleTarget<Drawable?>() {

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        binding.layout.background = resource
                    }
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = ItemCategoryBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList[position])
            }
        }
    }
}