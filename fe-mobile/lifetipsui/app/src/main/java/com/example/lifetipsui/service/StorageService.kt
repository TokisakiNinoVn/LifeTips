package com.example.lifetipsui.service

import com.google.gson.Gson
import java.io.*

object StorageService {
    private val gson = Gson()

    // Lưu file vào thư mục tạm thời (có thể ghi)
    private val FILE_PATH = System.getProperty("java.io.tmpdir") + File.separator + "localStorage.json"


    // Đảm bảo file tồn tại
    private fun ensureFileExists() {
        val file = File(FILE_PATH)
        // println("🔍 Ensuring file exists at: ${file.absolutePath}")
        println("📁 File FILE_PATH: $FILE_PATH")
        try {
            if (!file.exists()) {
                // Tạo thư mục cha nếu cần
                file.parentFile?.let {
                    if (!it.exists()) {
                        val dirCreated = it.mkdirs()
                        println("📁 Created parent directory: $dirCreated at ${it.absolutePath}")
                    }
                }

                val created = file.createNewFile()
                println(if (created) "✅ File created." else "⚠️ File already exists but couldn't be created again.")

                // Ghi dữ liệu trống ban đầu
                val empty = mapOf(
                    "token" to "",
                    "loginStatus" to "false"
                )
                BufferedWriter(FileWriter(file)).use { writer ->
                    gson.toJson(empty, writer)
                }
            } else {
                println("📦 File already exists.")
            }
        } catch (e: IOException) {
            println("❌ Lỗi khi tạo file token: ${e.message}")
            e.printStackTrace()
        }
    }

    // Đọc dữ liệu từ file
    private fun readData(): MutableMap<String, String> {
        ensureFileExists()
        return try {
            BufferedReader(FileReader(FILE_PATH)).use { reader ->
                val raw: Map<*, *>? = gson.fromJson(reader, Map::class.java)
                val data = mutableMapOf<String, String>()
                raw?.forEach { (k, v) -> data[k.toString()] = v.toString() }
                data
            }
        } catch (e: Exception) {
            println("❌ Lỗi khi đọc dữ liệu từ file:")
            e.printStackTrace()
            mutableMapOf()
        }
    }

    // Ghi dữ liệu vào file
    private fun writeData(data: Map<String, String>) {
        try {
            BufferedWriter(FileWriter(FILE_PATH)).use { writer ->
                gson.toJson(data, writer)
            }
            println("💾 Data written successfully.")
        } catch (e: IOException) {
            println("❌ Lỗi khi ghi dữ liệu vào file:")
            e.printStackTrace()
        }
    }

    fun saveToken(token: String) {
        val data = readData()
        data["token"] = token
        writeData(data)
        println("🔐 Token saved: $token")
    }

    fun getToken(): String {
        val data = readData()
        // println("🔐 Token read: ${data["token"]}")
        return data["token"] ?: ""
    }

    fun clearToken() {
        val data = readData()
        data["token"] = ""
        writeData(data)
        println("🧼 Token cleared.")
    }

    fun setStatusLogin(status: String) {
        val data = readData()
        data["loginStatus"] = status
        writeData(data)
        println("🔄 Login status set: $status")
    }

    fun getStatusLogin(): String {
        val data = readData()
        println("🔄 Login status read: ${data["loginStatus"]}")
        return data["loginStatus"] ?: "false"
    }

    fun logout() {
        clearToken()
        setStatusLogin("false")
        println("🚪 User logged out.")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println("📂 Using temp file path: $FILE_PATH")
        setStatusLogin("true")
        saveToken("abc123")
        println("✅ Read token = ${getToken()}")
        println("✅ Read login status = ${getStatusLogin()}")
        clearToken()
        println("✅ After clear token = ${getToken()}")
    }
}
