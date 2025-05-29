package com.example.lifetipsui.service

import com.example.lifetipsui.apis.AuthApi
import com.example.lifetipsui.helper.ApiMethodsPublic
import kotlinx.serialization.json.JsonObject

class AuthService {
    companion object {
        suspend fun login(data: Map<String, String>): JsonObject? {
            return try {
                val response = ApiMethodsPublic.post<JsonObject>(AuthApi.loginApi, data)
                response
            } catch (e: Exception) {
                println(">> [AuthService] Error calling login API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }

        suspend fun register(data: Map<String, String>): JsonObject? {
            return try {
                val response = ApiMethodsPublic.post<JsonObject>(AuthApi.registerApi, data)
                response
            } catch (e: Exception) {
                println(">> [AuthService] Error calling register API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }
    }
}

