package com.example.lifetipsui.service

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

import com.example.lifetipsui.apis.PostApi
import com.example.lifetipsui.helper.ApiMethodsPrivate
import com.example.lifetipsui.helper.ApiMethodsPublic

class PostService {
    companion object {
        suspend fun createPost(
            title: String,
            content: String,
            categoryId: Int,
            files: List<MultipartBody.Part>,
            isPrivate: Int
        ): JSONObject? {
            val token = StorageService.getToken() ?: return null
            val client = OkHttpClient()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", title)
                .addFormDataPart("content", content)
                .addFormDataPart("categoryId", categoryId.toString())
                .addFormDataPart("isPrivate", isPrivate.toString())
                .apply {
                    files.forEach { part ->
                        addPart(part)
                    }
                }
                .build()

            val request = Request.Builder()
                .url(PostApi.createPostApi)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            return try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                response.close()
                if (response.isSuccessful && responseBody != null) {
                    JSONObject(responseBody)
                } else {
                    null
                }
            } catch (e: Exception) {
                println(">> [PostService] Error: ${e.localizedMessage}")
                null
            }
        }

        // Lấy bài viết ngẫu nhiên
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getRandomPost(): JSONObject? {
            return try {
                val rawJson = ApiMethodsPublic.getRaw(PostApi.getRandomPostApi)
                JSONObject(rawJson)
            } catch (e: Exception) {
                println(">> [PostService] Error calling getRandomPost API: ${e.localizedMessage} - $e")
                e.printStackTrace()
                null
            }
        }

        // Lấy chi tiết bài viết theo id
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getPostById(postId: Int): JSONObject? {
            return try {
                // Gắn luôn id vào URL
                val urlWithId = "${PostApi.getByIdPostApi}/$postId"
                val rawJson = ApiMethodsPublic.getRaw(urlWithId)
                JSONObject(rawJson)
            } catch (e: Exception) {
                println(">> [PostService] Error calling getPostById API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }
        // Lưu bài viết
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun savePostService(body: JSONObject): JSONObject? {
            return try {
                ApiMethodsPrivate.postRequest(PostApi.savePostApi, body)
            } catch (e: Exception) {
                println(">> [PostService] Error calling savePost API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }

        // bỏ lưu bài viết
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun unsavePostService(postId: Int): JSONObject? {
            return try {
                val urlWithId = "${PostApi.unsavePostApi}/$postId"
                ApiMethodsPrivate.deleteRequest(urlWithId)
            } catch (e: Exception) {
                println(">> [PostService] Error calling unsavePost API: ${e.localizedMessage} + ${e.message} + ${e}")
                e.printStackTrace()
                null
            }
        }

        // Lấy danh sách bài viết đã lưu
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getSavedPostsService(): JSONObject? {
            return try {
                ApiMethodsPrivate.getRequest(PostApi.getAllSavePostApi)
            } catch (e: Exception) {
                println(">> [PostService] Error calling getSavedPosts API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }

        // Lấy danh sách bài viết của người dùng
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getUserPostsService(): JSONObject? {
            return try {
                ApiMethodsPrivate.getRequest(PostApi.getAllByUserPostApi)
            } catch (e: Exception) {
                println(">> [PostService] Error calling getUserPosts API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }
        // Xóa bài viết của người dùng
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun deletePostService(postId: Int): JSONObject? {
            return try {
                val urlWithId = "${PostApi.deletePostApi}/$postId"
                ApiMethodsPrivate.deleteRequest(urlWithId)
            } catch (e: Exception) {
                println(">> [PostService] Error calling deletePost API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }


    }
}