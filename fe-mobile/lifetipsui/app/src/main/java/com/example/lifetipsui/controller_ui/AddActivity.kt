package com.example.lifetipsui.controller_ui

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetipsui.R
import com.example.lifetipsui.service.CategoryService
import com.example.lifetipsui.service.PostService
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

data class Category(val id: Int, val name: String, val description: String)

class MediaAdapter(private val uris: MutableList<Uri>, private val context: Context) :
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.mediaImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media_preview, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.imageView.setImageURI(uris[position])
    }

    override fun getItemCount(): Int = uris.size
}

class AddActivity : AppCompatActivity() {

    private val selectedMediaUris = mutableListOf<Uri>()
    private lateinit var mediaRecyclerView: RecyclerView
    private lateinit var titleInput: TextInputEditText
    private lateinit var contentInput: TextInputEditText
    private lateinit var categoryInput: MaterialAutoCompleteTextView
    private lateinit var privacyRadioGroup: RadioGroup
    private lateinit var submitBtn: MaterialButton
    private lateinit var mediaAdapter: MediaAdapter

    private var categories = listOf<Category>()
    private var categoryMap = mutableMapOf<String, Int>() // name -> id

    private val mediaPickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris?.let {
            for (uri in it) {
                if (selectedMediaUris.size >= 5) {
                    Toast.makeText(this, "Chỉ được chọn tối đa 5 ảnh hoặc video", Toast.LENGTH_SHORT).show()
                    break
                }
                if (isVideo(uri) && getFileSize(uri) > 1_000_000_000L) {
                    Toast.makeText(this, "Video phải nhỏ hơn 1GB", Toast.LENGTH_SHORT).show()
                    continue
                }
                selectedMediaUris.add(uri)
                mediaAdapter.notifyItemInserted(selectedMediaUris.size - 1)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_post)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener { finish() }

        mediaRecyclerView = findViewById(R.id.mediaRecyclerView)
        titleInput = findViewById(R.id.editTitle)
        contentInput = findViewById(R.id.editContent)
        categoryInput = findViewById(R.id.editCategory)
        privacyRadioGroup = findViewById(R.id.privacyRadioGroup)
        submitBtn = findViewById(R.id.btnPost)

        // Setup RecyclerView
        mediaAdapter = MediaAdapter(selectedMediaUris, this)
        mediaRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mediaRecyclerView.adapter = mediaAdapter

        // Setup Category Dropdown
        fetchCategories()

        findViewById<MaterialButton>(R.id.btnSelectMedia).setOnClickListener {
            mediaPickerLauncher.launch("*/*")
        }

        submitBtn.setOnClickListener {
            submitPost()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchCategories() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    CategoryService.getListCategoryService()
                }
                println("List Category: $response")
                if (response != null && response.getString("status") == "success") {
                    val data = response.getJSONArray("data")
                    categories = (0 until data.length()).map {
                        val item = data.getJSONObject(it)
                        Category(
                            id = item.getInt("id"),
                            name = item.getString("name"),
                            description = item.getString("description")
                        )
                    }
                    categoryMap = categories.associate { it.name to it.id }.toMutableMap()
                    val names = categories.map { it.name }
                    val adapter = ArrayAdapter(this@AddActivity, android.R.layout.simple_dropdown_item_1line, names)
                    categoryInput.setAdapter(adapter)
                    if (names.isNotEmpty()) {
                        categoryInput.setText(names[0], false) // Set default selection
                    }
                } else {
                    Toast.makeText(this@AddActivity, "Không thể tải danh mục", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddActivity, "Lỗi tải danh mục: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileSize(uri: Uri): Long {
        var size: Long = 0
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (cursor.moveToFirst()) size = cursor.getLong(sizeIndex)
        }
        return size
    }

    private fun isVideo(uri: Uri): Boolean {
        return contentResolver.getType(uri)?.startsWith("video") == true
    }

    private fun submitPost() {
        val title = titleInput.text.toString().trim()
        val content = contentInput.text.toString().trim()
        val categoryName = categoryInput.text.toString().trim()
        val isPrivate = when (privacyRadioGroup.checkedRadioButtonId) {
            R.id.radioPublic -> 0
            R.id.radioPrivate -> 1
            else -> 0
        }

        if (title.isEmpty() || content.isEmpty() || categoryName.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ các trường", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryId = categoryMap[categoryName]
        if (categoryId == null) {
            Toast.makeText(this, "Danh mục không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val parts = selectedMediaUris.mapIndexed { index, uri ->
                    val inputStream = contentResolver.openInputStream(uri)
                    val fileName = "file_$index.${if (isVideo(uri)) "mp4" else "jpg"}"
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()
                    val requestFile = RequestBody.create(contentResolver.getType(uri)?.toMediaTypeOrNull(), bytes!!)
                    MultipartBody.Part.createFormData("files", fileName, requestFile)
                }

                val response = withContext(Dispatchers.IO) {
                    PostService.createPost(
                        title = title,
                        content = content,
                        categoryId = categoryId,
                        files = parts,
                        isPrivate = isPrivate
                    )
                }

                if (response != null && response.getString("status") == "success") {
                    Toast.makeText(this@AddActivity, "Đăng bài thành công!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@AddActivity, "Lỗi khi đăng bài: ${response?.getString("message")}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddActivity, "Lỗi khi đăng bài: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}