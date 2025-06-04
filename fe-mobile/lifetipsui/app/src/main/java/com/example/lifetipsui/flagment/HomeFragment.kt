package com.example.lifetipsui.flagment

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetipsui.R
import com.example.lifetipsui.adapter.PostAdapter
import com.example.lifetipsui.service.PostService
import com.example.lifetipsui.service.CategoryService
import com.example.lifetipsui.model.Post
import com.example.lifetipsui.model.Category
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var searchEditText: android.widget.EditText
    private lateinit var categorySpinner: android.widget.Spinner
    private val TAG = "HomeFragment"

    private var allPosts: List<Post> = emptyList()
    private var categories: List<Category> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        searchEditText = view.findViewById(R.id.searchEditText)
        categorySpinner = view.findViewById(R.id.categorySpinner)

        // Setup RecyclerView
        postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(mutableListOf(), viewLifecycleOwner)
        postRecyclerView.adapter = postAdapter

        // Load data
        loadRandomPosts()
        loadCategories()

        // Setup search functionality
        setupSearch()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadRandomPosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE

                val jsonObject = withContext(Dispatchers.IO) {
                    PostService.getRandomPost()
                }

                if (jsonObject != null) {
                    val dataArray = jsonObject.optJSONArray("data") ?: JSONArray()

                    val gson = Gson()
                    val type = object : TypeToken<List<Post>>() {}.type
                    val listPost: List<Post> = gson.fromJson(dataArray.toString(), type)

                    allPosts = listPost
                    postAdapter.updatePosts(listPost)
                } else {
                    Log.e(TAG, "Không nhận được dữ liệu từ API")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi tải bài viết: ${e.localizedMessage}", e)
                Toast.makeText(
                    requireContext(),
                    "Lỗi khi tải bài viết: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val jsonObject = withContext(Dispatchers.IO) {
                    CategoryService.getListCategoryService()
                }

                if (jsonObject != null) {
                    val dataArray = jsonObject.optJSONArray("data") ?: JSONArray()

                    val gson = Gson()
                    val type = object : TypeToken<List<Category>>() {}.type
                    categories = gson.fromJson(dataArray.toString(), type)

                    // Setup spinner
                    val categoryNames = categories.map { it.name }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        listOf("Tất cả danh mục") + categoryNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter

                    // Handle category selection
                    categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            filterPosts(searchEditText.text.toString(), position)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                } else {
                    Log.e(TAG, "Không nhận được dữ liệu danh mục từ API")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi tải danh mục: ${e.localizedMessage}", e)
                Toast.makeText(
                    requireContext(),
                    "Lỗi khi tải danh mục: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterPosts(s.toString(), categorySpinner.selectedItemPosition)
            }
        })
    }

    private fun filterPosts(query: String, selectedCategoryPosition: Int) {
        val filteredPosts = allPosts.filter { post ->
            // Filter by search query (name or description)
            val matchesSearch = query.isEmpty() ||
                    post.title?.contains(query, ignoreCase = true) == true ||
                    post.content?.contains(query, ignoreCase = true) == true

            // Filter by category
            val matchesCategory = selectedCategoryPosition == 0 || // "All categories" selected
                    (post.category?.name == categories[selectedCategoryPosition - 1].name)

            matchesSearch && matchesCategory
        }
        postAdapter.updatePosts(filteredPosts)
    }
}