package com.example.ui.service;

import com.example.ui.apis.AuthApi;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final OkHttpClient client = new OkHttpClient();

    public static String login(String email, String password) throws IOException {
        Gson gson = new Gson();

        // 🪄 Tạo một Map thay cho model
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("password", password);

        String json = gson.toJson(loginData); // giống như { email, password } trong JS

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(AuthApi.getLoginUrl())
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        }
    }
}
