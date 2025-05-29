package com.example.lifetipsui.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifetipsui.R
import com.example.lifetipsui.config.Config

data class MediaItem(val url: String, val type: String)

class MediaAdapter(private val mediaItems: List<MediaItem>) :
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePostItem)
        val videoView: VideoView = itemView.findViewById(R.id.videoPostItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaItem = mediaItems[position]
        val cleanedUrl = mediaItem.url.replace("\\", "/")
            .replaceFirst("public/", "", ignoreCase = true)
        val mediaUrl = "${Config.BASE_URL_IMAGE}/$cleanedUrl"

        if (mediaItem.type.startsWith("image/")) {
            holder.imageView.visibility = View.VISIBLE
            holder.videoView.visibility = View.GONE

            Glide.with(holder.itemView.context)
                .load(mediaUrl)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.imageView)

            holder.imageView.setOnClickListener {
                showFullScreenImage(holder.itemView.context, mediaUrl)
            }
        } else if (mediaItem.type.startsWith("video/")) {
            holder.imageView.visibility = View.GONE
            holder.videoView.visibility = View.VISIBLE

            holder.videoView.setVideoPath(mediaUrl)
            val mediaController = android.widget.MediaController(holder.itemView.context)
            mediaController.setAnchorView(holder.videoView)
            holder.videoView.setMediaController(mediaController)
            holder.videoView.setOnPreparedListener { it.setVolume(0f, 0f) } // Mute by default
            holder.videoView.start()
        }
    }

    override fun getItemCount(): Int = mediaItems.size

    private fun showFullScreenImage(context: android.content.Context, imageUrl: String) {
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_fullscreen_image)

        val fullscreenImage = dialog.findViewById<ImageView>(R.id.fullscreenImage)
        val closeButton = dialog.findViewById<android.widget.ImageButton>(R.id.closeButton)

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.img)
            .error(R.drawable.img)
            .into(fullscreenImage)

        closeButton.setOnClickListener { dialog.dismiss() }
//        dialog.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}