package com.example.lifetipsui.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lifetipsui.apis.CategoryApi
import com.example.lifetipsui.apis.NotificationApi
import com.example.lifetipsui.helper.ApiMethodsPrivate
import org.json.JSONObject

annotation class NotificationService {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getListNotificationService(): JSONObject? {
            return try {
                ApiMethodsPrivate.getRequest(NotificationApi.getListNotificationApi)
            } catch (e: Exception) {
                println(">> [NotificationService] Error calling getListNotificationService API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }
    }
}
