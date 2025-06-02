package com.example.ui.service;

import com.example.ui.apis.PostApi;
import com.example.ui.config.ApiMethodsPrivate;
import com.example.ui.config.ApiMethodsPublic;
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

public class NotificationService {
    // Lấy danh sách thông báo
    public static JSONObject getAllNotificationService() {
        String url = PostApi.getAllNotificationApi;
        return ApiMethodsPrivate.getRequest(url);
    }
}
