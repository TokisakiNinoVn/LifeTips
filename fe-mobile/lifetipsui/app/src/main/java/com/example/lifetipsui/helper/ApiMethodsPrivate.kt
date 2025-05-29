package com.example.lifetipsui.helper

import com.example.lifetipsui.service.StorageService
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ApiMethodsPrivate {
    companion object {

        private fun makeRequest(
            urlString: String,
            method: String,
            body: JSONObject? = null
        ): JSONObject {
            return try {
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = method
                conn.setRequestProperty("Content-Type", "application/json")

                // Gắn token nếu có
                val token = StorageService.getToken()
                token?.takeIf { it.isNotEmpty() }?.let {
                    conn.setRequestProperty("Authorization", "Bearer $it")
                }

                // Gửi body nếu có (POST/PUT)
                if (method == "POST" || method == "PUT") {
                    conn.doOutput = true
                    conn.outputStream.use { os ->
                        val input = body.toString().toByteArray(StandardCharsets.UTF_8)
                        os.write(input, 0, input.size)
                    }
                }

                // Đọc kết quả
                val statusCode = conn.responseCode
                val reader = BufferedReader(
                    InputStreamReader(
                        if (statusCode in 200..299) conn.inputStream else conn.errorStream,
                        StandardCharsets.UTF_8
                    )
                )

                val responseBuilder = StringBuilder()
                reader.forEachLine { responseBuilder.append(it.trim()) }
                val response = responseBuilder.toString()

                val jsonResponse = if (response.isNotEmpty()) JSONObject(response) else JSONObject()

                if (statusCode in 200..299) {
                    jsonResponse
                } else {
                    JSONObject().apply {
                        put("status", "error")
                        put("message", jsonResponse.optString("message", "Unknown error."))
                    }
                }

            } catch (e: Exception) {
                System.err.println("API Error: ${e.message}")
                println("API Error: ${e.message} - $e")
                JSONObject().apply {
                    put("status", "error")
                    put("message", "Cannot connect to the server. Please try again later: $e")
                }
            }
        }

        // GET request với dữ liệu query params
        fun getRequest(url: String, data: Map<String, String>? = null): JSONObject {
            println("Calling API URL: $url")
            val fullUrl = if (!data.isNullOrEmpty()) {
                val queryParams = data.entries.joinToString("&") { (key, value) ->
                    "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
                }
                "$url?$queryParams"
            } else {
                url
            }
            return makeRequest(fullUrl, "GET")
        }

        fun postRequest(url: String, body: JSONObject): JSONObject =
            makeRequest(url, "POST", body)

        fun putRequest(url: String, body: JSONObject): JSONObject =
            makeRequest(url, "PUT", body)

        fun deleteRequest(url: String): JSONObject =
            makeRequest(url, "DELETE")
    }
}
