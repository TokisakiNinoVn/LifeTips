package com.example.lifetipsui.service

import com.example.lifetipsui.apis.UserApi
import com.example.lifetipsui.helper.ApiMethodsPrivate
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

class UserService {
    companion object {
        // Hàm lấy thông tin người dùng
        suspend fun getInfor(): JSONObject? {
            return try {
                ApiMethodsPrivate.getRequest(UserApi.getInforApi)
            } catch (e: Exception) {
                println(">> [UserService] Error calling getInfor API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }

        suspend fun updateInfor(data: Map<String, String>): JSONObject? {
            return try {
                val jsonBody = JSONObject()
                for ((key, value) in data) {
                    jsonBody.put(key, value)
                }

                val response = ApiMethodsPrivate.putRequest(UserApi.updateInforApi, jsonBody)
                response
            } catch (e: Exception) {
                println(">> [UserService] Error calling updateInfor API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }

    }
}
