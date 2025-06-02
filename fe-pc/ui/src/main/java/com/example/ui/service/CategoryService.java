package com.example.ui.service;

import com.example.ui.apis.CategoryApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;

public class CategoryService {
    private static final OkHttpClient client = new OkHttpClient();

    // Lấy danh sách tất cả các danh mục
    public static JSONObject getAllCategory() {
        String url = CategoryApi.getAllCategoryEndpoint();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonString = response.body().string();
                return new JSONObject(jsonString);  // Sử dụng org.json.JSONObject
            } else {
                System.err.println("Lỗi khi gọi API: " + response.code());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
