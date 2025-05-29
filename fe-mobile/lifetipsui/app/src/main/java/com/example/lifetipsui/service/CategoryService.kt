package com.example.lifetipsui.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lifetipsui.apis.CategoryApi
import com.example.lifetipsui.helper.ApiMethodsPublic
import org.json.JSONObject

class CategoryService {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getListCategoryService(): JSONObject? {
            return try {
                val rawJson = ApiMethodsPublic.getRaw(CategoryApi.getListCategoryApi)
                JSONObject(rawJson) // <-- Parse JSON như bạn muốn
            } catch (e: Exception) {
                println(">> [CategoryService] Error calling getListCategoryService API: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }
        }
    }
}


//class CategoryService {
//    companion object {
//        suspend fun getListCategoryService(): JSONObject? {
//            return try {
//                ApiMethodsPublic.get(CategoryApi.getListCategoryApi)
//            } catch (e: Exception) {
//                println(">> [CategoryService] Error calling getListCategoryService API: ${e.localizedMessage}")
//                e.printStackTrace()
//                null
//            }
//        }
//    }
//}