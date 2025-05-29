package com.example.lifetipsui.controller_ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifetipsui.R
import com.example.lifetipsui.adapter.ImageAdapter
import com.example.lifetipsui.config.Config
import com.example.lifetipsui.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import com.example.lifetipsui.service.PostService
import com.example.lifetipsui.service.CommentService
import com.example.lifetipsui.adapter.CommentAdapter

class DetailActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val postId = intent.getIntExtra("postId", -1)

        val textPostId = findViewById<TextView>(R.id.textPostId)
        val textTitle = findViewById<TextView>(R.id.textTitle)
        val textContent = findViewById<TextView>(R.id.textContent)
        val textAuthor = findViewById<TextView>(R.id.textAuthor)
        val textCategory = findViewById<TextView>(R.id.textCategory)
        val recyclerViewImages = findViewById<RecyclerView>(R.id.recyclerViewImages)
        val textCreatedAt = findViewById<TextView>(R.id.textCreatedAt)
        val textPrivacy = findViewById<TextView>(R.id.textPrivacy)
        val imageAvatar = findViewById<ImageView>(R.id.imageAvatar)

        val btnSend = findViewById<ImageButton>(R.id.buttonSend)
        val spinnerRating = findViewById<Spinner>(R.id.spinnerRating)
        val contentCommnet = findViewById<TextView>(R.id.editTextComment)

        val recyclerViewComments = findViewById<RecyclerView>(R.id.recyclerViewComments)
        recyclerViewComments.layoutManager = LinearLayoutManager(this)

        recyclerViewImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            try {
                // Lấy dữ liệu bài viết và bình luận
                val jsonObject = withContext(Dispatchers.IO) {
                    PostService.getPostById(postId)
                }

                val commentsJsonObject = withContext(Dispatchers.IO) {
                    CommentService.getAllCommentOfPostService(postId)
                }

                val dataPost = jsonObject?.getJSONObject("data") ?: return@launch

                // --- Hiển thị dữ liệu bài viết ---
                val title = dataPost.optString("title", "Không có tiêu đề")
                val content = dataPost.optString("content", "Không có nội dung")
                val user = dataPost.optJSONObject("user")
                val authorName = user?.optString("full_name", "Ẩn danh") ?: "Ẩn danh"
                val category = dataPost.optJSONObject("inforCategoryOfPost")
                val categoryName = category?.optString("name", "Không có chuyên mục") ?: "Không có chuyên mục"
                val createdAt = dataPost.optString("created_at", "")
                val isPrivate = dataPost.optInt("is_private", 0)
                val avatarPath = user?.optString("avatar", "default-avatar.png") ?: "default-avatar.png"

                val formattedDate = try {
                    val zonedDateTime = ZonedDateTime.parse(createdAt)
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    zonedDateTime.format(formatter)
                } catch (e: Exception) {
                    createdAt
                }

                textTitle.text = "Tiêu đề: $title"
                textContent.text = "Nội dung: $content"
                textAuthor.text = "Người đăng: $authorName"
                textCategory.text = "Chuyên mục: $categoryName"
                textCreatedAt.text = "Ngày đăng: $formattedDate"
                textPrivacy.text = "Chế độ: ${if (isPrivate == 1) "Riêng tư" else "Công khai"}"

                val avatarUrl = "${Config.BASE_URL_IMAGE}/images/accounts/$avatarPath"
                Glide.with(this@DetailActivity)
                    .load(avatarUrl)
                    .placeholder(R.drawable.img)
                    .error(R.drawable.img)
                    .into(imageAvatar)

                // --- Hiển thị hình ảnh trong bài viết ---
                val filesArray = dataPost.optJSONArray("filesNormal")
                val imageUrls = mutableListOf<String>()
                if (filesArray != null) {
                    for (i in 0 until filesArray.length()) {
                        val file = filesArray.getJSONObject(i)
                        val rawUrl = file.optString("url", "")
                        if (rawUrl.isNotEmpty()) {
                            imageUrls.add(rawUrl)
                        }
                    }
                }

                if (imageUrls.isNotEmpty()) {
                    recyclerViewImages.adapter = ImageAdapter(imageUrls)
                } else {
                    recyclerViewImages.visibility = View.GONE
                }

                // --- Xử lý danh sách bình luận ---
                val commentsDataArray = commentsJsonObject?.getJSONArray("data") ?: JSONArray()
                val commentList = mutableListOf<Comment>()

                for (i in 0 until commentsDataArray.length()) {
                    val item = commentsDataArray.getJSONObject(i)
                    val commentContent = item.optString("content", "Không có nội dung")
                    val rate = item.optInt("rate", 0)
                    val commentUser = item.optJSONObject("user")
                    val commentAuthor = item.optString("full_name")
                    val commentCreatedAt = item.optString("created_at", "")
                    val commentFormattedDate = try {
                        ZonedDateTime.parse(commentCreatedAt).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    } catch (e: Exception) {
                        commentCreatedAt
                    }

                    commentList.add(
                        Comment(
                            content = commentContent,
                            fullName = commentAuthor,
                            createdAt = commentFormattedDate,
                            rate = rate
                        )
                    )
                }

                if (commentList.isNotEmpty()) {
                    recyclerViewComments.adapter = CommentAdapter(commentList)
                } else {
                    recyclerViewComments.visibility = View.GONE
                }

            } catch (e: Exception) {
                println("Lỗi khi lấy dữ liệu post hoặc bình luận: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }

        btnSend.setOnClickListener {
            val selectedRating = spinnerRating.selectedItem.toString()
            val content = contentCommnet.text.toString()
            // Cắt lấy số từ rating
            val rating = selectedRating.filter { it.isDigit() }.toIntOrNull() ?: 0
            lifecycleScope.launch {
                try {
                    // Gọi API gửi bình luận
                    val response = withContext(Dispatchers.IO) {
                        CommentService.createCommentService(postId, mapOf(
                            "content" to content,
                            "rate" to rating.toString()
                        ))
                    }

                    if (response != null) {
                        contentCommnet.setText("")
                        spinnerRating.setSelection(0)
                        recyclerViewComments.adapter?.notifyDataSetChanged()
                        Toast.makeText(this@DetailActivity, "Gửi bình luận thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        // Hiển thị thông báo lỗi
                        println("Lỗi khi gửi bình luận: ${response?.getString("message")}")
                    }
                } catch (e: Exception) {
                    println("Lỗi khi gửi bình luận: ${e.localizedMessage}")
                }
            }

        }
    }
}