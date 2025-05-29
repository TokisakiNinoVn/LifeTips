package com.example.lifetipsui.service

import com.example.lifetipsui.apis.CommentApi
import com.example.lifetipsui.helper.ApiMethodsPrivate
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

class CommentService {
    companion object {
        suspend fun getAllCommentOfPostService(postId: Int): JSONObject? {
            return try {
                val urlWithId = "${CommentApi.getAllCommentByIdPostApi}/$postId"
                ApiMethodsPrivate.getRequest(urlWithId)
            } catch (e: Exception) {
                println(">> [CommentService] Error calling getAllCommentByIdPostApi API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }

        suspend fun createCommentService(postId: Int, data: Map<String, String>): JSONObject? {
            return try {
                val jsonBody = JSONObject()
                for ((key, value) in data) {
                    jsonBody.put(key, value)
                }
                val url = "${CommentApi.createCommentApi}/$postId"
                val response = ApiMethodsPrivate.postRequest(url, jsonBody)
                response
            } catch (e: Exception) {
                println(">> [CommentService] Error calling createCommentApi API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }

    }
}
