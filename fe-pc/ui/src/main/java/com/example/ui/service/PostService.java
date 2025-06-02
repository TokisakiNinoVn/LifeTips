package com.example.ui.service;

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
import java.util.List;
import java.util.stream.Collectors;

import com.example.ui.config.ApiMethodsPrivate;
import com.example.ui.config.ApiMethodsPublic;
import com.example.ui.apis.PostApi;

public class PostService {
    private static String token = StorageService.getToken();
    // Tạo bài viết mới
    public static String createPost(String title, String content, int categoryIds, List<File> mediaFiles, int visibility) {
        String url = PostApi.createPostEndpoint();

        try {
            HttpPost postRequest = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            // Add text fields
            builder.addTextBody("title", title, ContentType.TEXT_PLAIN);
            builder.addTextBody("content", content, ContentType.TEXT_PLAIN);
            builder.addTextBody("isPrivate", String.valueOf(visibility), ContentType.TEXT_PLAIN);
            builder.addTextBody("categoryId", String.valueOf(categoryIds), ContentType.TEXT_PLAIN);

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
        String url = PostApi.GETALLPOST_ENDPOINT;
        return ApiMethodsPublic.getRequest(url, null);
    }

    // Lấy thông tin chi tiết bài viết
    public static JSONObject getPostById(int id) {
        String url = PostApi.GETPOSTBYID_ENDPOINT;
        return ApiMethodsPublic.getRequest(url, id);
    }

    // Tạo bình luận
    public static JSONObject createComment(int postId, JSONObject body) {
        String url = PostApi.createCommentPostApi + "/" + postId;
        return ApiMethodsPrivate.postRequest(url, body);
    }

    // Lưu bài viết
    public static JSONObject savePost(JSONObject body) {
        String url = PostApi.savePostApi;
        return ApiMethodsPrivate.postRequest(url, body);
    }

    // Lấy danh sách bài viết đã lưu
    public static JSONObject getSavedPosts() {
        String url = PostApi.getAllSavePostApi;
        return ApiMethodsPrivate.getRequest(url);
    }

    // Xóa bài viết
    public static JSONObject deletePost(int postId) {
        String url = PostApi.DELETEPOST_ENDPOINT + "/" + postId;
        return ApiMethodsPrivate.deleteRequest(url);
    }

    // Cập nhật bài viết
    public static JSONObject updatePost(int postId, JSONObject body) {
        String url = PostApi.UPDATEPOST_ENDPOINT + "/" + postId;
        return ApiMethodsPrivate.putRequest(url, body);
    }

    // Lấy các bài viết của người dùng
    public static JSONObject getUserPostsService() {
        String url = PostApi.getAllPostUserApi;
        return ApiMethodsPrivate.getRequest(url);
    }

    // Bỏ lưu bài viết
    public static JSONObject unsavePost(int postId) {
        String url = PostApi.unavePostApi + "/" + postId;
        return ApiMethodsPrivate.deleteRequest(url);
    }
}
