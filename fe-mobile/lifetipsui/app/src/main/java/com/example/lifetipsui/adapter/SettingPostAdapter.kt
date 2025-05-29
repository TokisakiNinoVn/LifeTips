package com.example.lifetipsui.adapter

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifetipsui.R
import com.example.lifetipsui.config.Config
import com.example.lifetipsui.controller_ui.DetailActivity
import com.example.lifetipsui.controller_ui.EditPostActivity
import com.example.lifetipsui.service.PostService
import com.example.lifetipsui.model.Post
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingPostAdapter(
    private val posts: MutableList<Post>,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<SettingPostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagePost: ImageView = itemView.findViewById(R.id.imagePost)
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textContent: TextView = itemView.findViewById(R.id.textContent)
        val textUser: TextView = itemView.findViewById(R.id.textUser)
        val textCategory: TextView = itemView.findViewById(R.id.textCategory)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        val btnViewDetails: ImageView = itemView.findViewById(R.id.btnViewDetails)
        val btnEdit : ImageView = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting_post, parent, false)
        return PostViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.textTitle.text = post.title ?: "Không có tiêu đề"
        holder.textContent.text = post.content ?: "Không có nội dung"
        holder.textUser.text = post.user?.full_name?.let { "Tác giả: $it" } ?: "Tác giả không xác định"
        holder.textCategory.text = post.category?.name?.let { "Chuyên mục: $it" } ?: "Không có chuyên mục"

        val rawUrl = post.files.firstOrNull()?.url
        if (!rawUrl.isNullOrEmpty()) {
            val cleanedUrl = reLinkImage(rawUrl)
            val imageUrl = Config.BASE_URL_IMAGE + "/" + cleanedUrl

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.img_1)
                .error(R.drawable.img_1)
                .into(holder.imagePost)
        } else {
            holder.imagePost.setImageResource(R.drawable.img_1)
        }

        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            val postId = post.id

            AlertDialog.Builder(context)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn xóa bài viết này không?")
                .setPositiveButton("Có") { dialog, _ ->
                    // Nếu người dùng xác nhận
                    lifecycleOwner.lifecycleScope.launch {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                PostService.deletePostService(postId)
                            }
                            if (response != null) {
                                Toast.makeText(context, "Đã xóa bài viết!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Xóa bài viết thất bại", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        holder.btnEdit.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditPostActivity::class.java)
            intent.putExtra("postId", post.id)
            Toast.makeText(context, "postId: ${post.id}", Toast.LENGTH_SHORT).show()
            context.startActivity(intent)
        }

        holder.btnViewDetails.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("postId", post.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    private fun reLinkImage(url: String): String {
        return url.replace("\\", "/")
            .replaceFirst("public/", "", ignoreCase = true)
    }
}

