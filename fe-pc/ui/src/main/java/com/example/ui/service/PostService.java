package com.example.ui.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import com.example.ui.apis.PostApi;
import com.example.ui.config.ApiMethodsPrivate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.util.List;
import java.util.stream.Collectors;

public class PostService {
    // private static final OkHttpClient client = new OkHttpClient();
    private static String token = StorageService.getToken();
    // Tạo bài viết mới
    public static String createPost(String title, String content, List<String> categoryIds, List<File> mediaFiles) {
        String url = PostApi.createPostEndpoint();

        try {
            HttpPost postRequest = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            // Add text fields
            builder.addTextBody("title", title, ContentType.TEXT_PLAIN);
            builder.addTextBody("content", content, ContentType.TEXT_PLAIN);

            // Add categories
            for (String catId : categoryIds) {
                builder.addTextBody("categoryId", catId, ContentType.TEXT_PLAIN);
            }

            // Add files
            for (File file : mediaFiles) {
                builder.addBinaryBody(
                        "files",
                        new FileInputStream(file),
                        ContentType.DEFAULT_BINARY,
                        file.getName()
                );
            }

            HttpEntity entity = builder.build();
            postRequest.setEntity(entity);

            // ✅ Thêm header Authorization với Bearer token
            if (token != null && !token.isEmpty()) {
                postRequest.setHeader("Authorization", "Bearer " + token);
            } else {
                System.out.println("⚠️ Token is null or empty. Authorization header not set.");
            }

            // Gửi request
            CloseableHttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(postRequest);

            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                responseBody = reader.lines().collect(Collectors.joining("\n"));
            }

            System.out.println("Status: " + statusCode);
            return responseBody;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while sending request: " + e.getMessage();
        }
    }

    // Lấy danh sách bài viết
    public static JSONObject getPosts() {
        String url = PostApi.getAllPostEndpoint();
        return ApiMethodsPrivate.getRequest(url);
    }
}
