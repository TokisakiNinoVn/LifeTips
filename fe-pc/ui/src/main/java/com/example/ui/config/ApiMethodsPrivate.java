package com.example.ui.config;

import com.example.ui.service.StorageService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class ApiMethodsPrivate {
    private static String token = StorageService.getToken();

    // Phương thức POST với token
    public static JSONObject postRequest(String urlString, JSONObject body) {
        try {
            // Tạo URL và mở kết nối
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Thiết lập phương thức và header
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            if (token != null && !token.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }
            conn.setDoOutput(true);

            // Gửi dữ liệu JSON
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            // Đọc phản hồi
            int statusCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            statusCode >= 200 && statusCode < 300
                                    ? conn.getInputStream()
                                    : conn.getErrorStream(),
                            StandardCharsets.UTF_8));

            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }

            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());

            // Xử lý response
            if (statusCode == 200 || statusCode == 201) {
                return jsonResponse;
            } else {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", jsonResponse.optString("message", "Unknown error."));
                return error;
            }

        } catch (Exception e) {
            // In lỗi (nếu cần)
            System.err.println("API Error: " + e.getMessage());

            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Cannot connect to the server. Please try again later: " + e.getMessage());
            return error;
        }
    }

    // Phương thức GET với token
    public static JSONObject getRequest(String urlString) {
        try {
            // Tạo URL và mở kết nối
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Thiết lập phương thức và header
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            if (token != null && !token.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Đọc phản hồi
            int statusCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            statusCode >= 200 && statusCode < 300
                                    ? conn.getInputStream()
                                    : conn.getErrorStream(),
                            "utf-8"));

            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }

            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());

            // Xử lý response
            if (statusCode == 200) {
                return jsonResponse;
            } else {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", jsonResponse.optString("message", "Unknown error."));
                return error;
            }

        } catch (Exception e) {
            // In lỗi (nếu cần)
            System.err.println("API Error: " + e.getMessage());

            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Cannot connect to the server. Please try again later: " + e.getMessage());
            return error;
        }
    }

    // Phương thức PUT với token
    public static JSONObject putRequest(String urlString, JSONObject body) {
        try {
            // Tạo URL và mở kết nối
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Thiết lập phương thức và header
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            if (token != null && !token.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }
            conn.setDoOutput(true);

            // Gửi dữ liệu
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Đọc phản hồi
            int statusCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            statusCode >= 200 && statusCode < 300
                                    ? conn.getInputStream()
                                    : conn.getErrorStream(),
                            "utf-8"));

            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }

            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());

            // Xử lý response
            if (statusCode == 200 || statusCode == 204) {
                return jsonResponse;
            } else {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", jsonResponse.optString("message", "Unknown error."));
                return error;
            }

        } catch (Exception e) {
            // In lỗi (nếu cần)
            System.err.println("API Error: " + e.getMessage());

            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Cannot connect to the server. Please try again later: " + e.getMessage());
            return error;
        }
    }

    // Phương thức DELETE với token
    public static JSONObject deleteRequest(String urlString) {
        try {
            // Tạo URL và mở kết nối
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Thiết lập phương thức và header
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");
            if (token != null && !token.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Đọc phản hồi
            int statusCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            statusCode >= 200 && statusCode < 300
                                    ? conn.getInputStream()
                                    : conn.getErrorStream(),
                            "utf-8"));

            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }

            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());

            // Xử lý response
            if (statusCode == 200 || statusCode == 204) {
                return jsonResponse;
            } else {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", jsonResponse.optString("message", "Unknown error."));
                return error;
            }

        } catch (Exception e) {
            // In lỗi (nếu cần)
            System.err.println("API Error: " + e.getMessage());

            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Cannot connect to the server. Please try again later: " + e.getMessage());
            return error;
        }
    }
}
