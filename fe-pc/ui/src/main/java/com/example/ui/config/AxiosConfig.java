package com.example.ui.config;

// file: src/services/AxiosConfig.java
import com.example.ui.service.StorageService;
import okhttp3.*;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class AxiosConfig {
    private static final OkHttpClient client;
    static {
        client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        // Lấy token từ file localStorage.txt
                        // Giả sử bạn đã có một phương thức để lấy token từ file
                        String token = StorageService.getToken();

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Content-Type", "application/json");

                        if (token != null && !token.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        }

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();
    }
}
