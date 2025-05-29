package com.example.lifetipsui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifetipsui.R
import com.example.lifetipsui.config.Config

class ImageAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePostItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val cleanedUrl = imageUrls[position].replace("\\", "/")
            .replaceFirst("public/", "", ignoreCase = true)
        val imageUrl = "${Config.BASE_URL_IMAGE}/$cleanedUrl"
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.img)
            .error(R.drawable.img)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = imageUrls.size
}