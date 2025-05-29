package com.example.lifetipsui.flagment

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
import com.example.lifetipsui.adapter.SavePostAdapter
import com.example.lifetipsui.model.Post

import com.example.lifetipsui.service.PostService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class SavedFragment : Fragment(R.layout.fragment_save) {
    private lateinit var savePostRecyclerView: RecyclerView
    private lateinit var savePostAdapter: SavePostAdapter
    private lateinit var progressBar: android.widget.ProgressBar
    private val TAG = "SavedFragment"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo RecyclerView
        savePostRecyclerView = view.findViewById(R.id.savePostRecyclerView)
        savePostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            savePostAdapter = SavePostAdapter(mutableListOf(), viewLifecycleOwner)
        }

        savePostRecyclerView.adapter = savePostAdapter
        progressBar = view.findViewById(R.id.progressBar)
        loadSavePosts()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadSavePosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE

                val jsonObject = withContext(Dispatchers.IO) {
                    PostService.getSavedPostsService()
                }
                // println("jsonObject list post save: $jsonObject")

                if (jsonObject != null) {
                    val dataArray = jsonObject.optJSONArray("data") ?: JSONArray()

                    val gson = Gson()
                    val type = object : TypeToken<List<Post>>() {}.type
                    val listPost: List<Post> = gson.fromJson(dataArray.toString(), type)

                    savePostAdapter.updatePosts(listPost)
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