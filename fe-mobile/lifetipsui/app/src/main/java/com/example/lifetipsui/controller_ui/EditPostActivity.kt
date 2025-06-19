package com.example.lifetipsui.controller_ui

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lifetipsui.R
import com.example.lifetipsui.service.CategoryService
import com.example.lifetipsui.service.PostService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import android.widget.RadioGroup
import android.widget.RadioButton

class EditPostActivity : AppCompatActivity() {

    private lateinit var editTitle: TextInputEditText
    private lateinit var editContent: TextInputEditText
    private lateinit var editCategory: MaterialAutoCompleteTextView
    private lateinit var privacyRadioGroup: RadioGroup
    private lateinit var radioPublic: RadioButton
    private lateinit var radioPrivate: RadioButton
    private lateinit var btnPost: MaterialButton
    private var selectedCategoryId: Int = -1
    private var postId: Int = -1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        // Initialize UI elements
        editTitle = findViewById(R.id.editTitle)
        editContent = findViewById(R.id.editContent)
        editCategory = findViewById(R.id.editCategory)
        privacyRadioGroup = findViewById(R.id.privacyRadioGroup)
        radioPublic = findViewById(R.id.radioPublic)
        radioPrivate = findViewById(R.id.radioPrivate)
        btnPost = findViewById(R.id.btnPost)

        // Get postId from intent
        postId = intent.getIntExtra("postId", -1)
        if (postId == -1) {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                // Fetch post data
                val responsePostJson = withContext(Dispatchers.IO) {
                    PostService.getPostById(postId)
                } ?: throw Exception("Failed to fetch post data")

                // Populate UI with post data
                populatePostData(responsePostJson)

                // Fetch and populate category dropdown
                val responseCategoriesJson = withContext(Dispatchers.IO) {
                    CategoryService.getListCategoryService()
                } ?: throw Exception("Failed to fetch categories")

                val dataPost = responsePostJson.getJSONObject("data")
                val categoryId = dataPost.getInt("category_id")
                populateCategoryDropdown(responseCategoriesJson, categoryId)

            } catch (e: Exception) {
                Toast.makeText(this@EditPostActivity, "Error fetching data: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        // Handle back button in toolbar
        findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            finish()
        }

        // Handle save/post button click
        btnPost.setOnClickListener {
            savePost()
        }
    }

    private fun populatePostData(jsonObject: JSONObject?) {
        jsonObject?.getJSONObject("data")?.let { data ->
            editTitle.setText(data.getString("title"))
            editContent.setText(data.getString("content"))
            val isPrivate = data.getInt("is_private")
            if (isPrivate == 1) {
                radioPrivate.isChecked = true
            } else {
                radioPublic.isChecked = true
            }
        }
    }

    private fun populateCategoryDropdown(categoriesJson: JSONObject, selectedCategoryId: Int) {
        val categoryNames = mutableListOf<String>()
        val categoryIds = mutableListOf<Int>()
        var selectedIndex = 0

        // Parse categories
        val categoriesArray = categoriesJson.getJSONArray("data")
        for (i in 0 until categoriesArray.length()) {
            val category = categoriesArray.getJSONObject(i)
            val id = category.getInt("id")
            val name = category.getString("name")
            categoryNames.add(name)
            categoryIds.add(id)
            if (id == selectedCategoryId) {
                selectedIndex = i
            }
        }

        // Set up adapter for dropdown
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryNames)
        editCategory.setAdapter(adapter)
        editCategory.setText(categoryNames[selectedIndex], false)
        this.selectedCategoryId = categoryIds[selectedIndex]

        // Update selected category ID when user selects a new category
        editCategory.setOnItemClickListener { _, _, position, _ ->
            this.selectedCategoryId = categoryIds[position]
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePost() {
        val title = editTitle.text.toString().trim()
        val content = editContent.text.toString().trim()
        val isPrivate = if (radioPrivate.isChecked) 1 else 0

        if (title.isEmpty() || content.isEmpty() || selectedCategoryId == -1) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("title", title)
                    put("content", content)
                    put("categoryId", selectedCategoryId)
                    put("is_private", isPrivate)
                }

                val response = withContext(Dispatchers.IO) {
                    PostService.updatePostService(postId, jsonObject)
                } ?: throw Exception("Failed to update post")

                Toast.makeText(this@EditPostActivity, "Post updated successfully", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@EditPostActivity, "Error updating post: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}