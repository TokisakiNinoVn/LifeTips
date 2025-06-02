package com.example.ui.config;

import org.json.JSONObject;

public class ApiMethodsPublic {
    // Phương thức GET có thể truyền thêm id hoặc không
    public static JSONObject getRequest(String urlString, Integer id) {
        try {
            // Nếu có id thì nối vào URL
            if (id != null) {
                if (!urlString.endsWith("/")) {
                    urlString += "/";
                }
                urlString += id;
            }

            // Tạo URL và mở kết nối
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();

            // Thiết lập phương thức và header
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Đọc phản hồi
            int statusCode = conn.getResponseCode();
            java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(
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
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return error;
        }
    }

    // Phương thức POST
    public static JSONObject postRequest(String urlString, JSONObject body) {
        try {
            // Tạo URL và mở kết nối
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();

            // Thiết lập phương thức và header
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Gửi dữ liệu
            if (body != null) {
                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = body.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            // Đọc phản hồi
            int statusCode = conn.getResponseCode();
            java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(
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
            if (statusCode == 200 || statusCode == 201) {
                return jsonResponse;
            } else {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", jsonResponse.optString("message", "Unknown error."));
                return error;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return error;
        }
    }
}


