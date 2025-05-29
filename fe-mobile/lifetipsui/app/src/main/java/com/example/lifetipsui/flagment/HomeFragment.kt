package com.example.lifetipsui.flagment

import okhttp3.ResponseBody

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetipsui.R
import com.example.lifetipsui.adapter.PostAdapter
import com.example.lifetipsui.service.PostService
import com.example.lifetipsui.model.Post
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
    private val TAG = "HomeFragment"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo RecyclerView
        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            postAdapter = PostAdapter(mutableListOf(), viewLifecycleOwner)
        }

        postRecyclerView.adapter = postAdapter

        // Khởi tạo ProgressBar
        progressBar = view.findViewById(R.id.progressBar)

        // Gọi API và cập nhật dữ liệu
        loadRandomPosts()
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



}