package com.example.ui.service;

import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Dịch vụ lưu trữ token và trạng thái đăng nhập vào file JSON cục bộ trong thư
 * mục dự án.
 */
public class StorageService {
    private static final Gson gson = new Gson();
    private static final String FILE_PATH = "localStorage.json";

    // Đảm bảo file tồn tại
    private static void ensureFileExists() {
        File file = new File(FILE_PATH);
        // System.out.println("Token file path: " + file.getAbsolutePath());

        try {
            if (!file.exists()) {
                boolean created = file.createNewFile();
                // System.out.println("Created token file: " + created);

                Map<String, String> empty = new HashMap<>();
                empty.put("token", "");
                empty.put("loginStatus", "false");
                try (Writer writer = new BufferedWriter(new FileWriter(file))) {
                    gson.toJson(empty, writer);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi tạo file token:");
            e.printStackTrace();
        }
    }

    // Đọc toàn bộ dữ liệu JSON từ file
    private static Map<String, String> readData() {
        ensureFileExists();
        try (Reader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            Map<?, ?> raw = gson.fromJson(reader, Map.class);
            Map<String, String> data = new HashMap<>();
            if (raw != null) {
                raw.forEach((k, v) -> data.put(String.valueOf(k), String.valueOf(v)));
            }
            return data;
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc dữ liệu từ file:");
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Ghi toàn bộ dữ liệu JSON vào file
    private static void writeData(Map<String, String> data) {
        try (Writer writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi dữ liệu vào file:");
            e.printStackTrace();
        }
    }

    // Lưu token (giữ lại loginStatus nếu có)
    public static void saveToken(String token) {
        Map<String, String> data = readData();
        data.put("token", token);
        writeData(data);
        // System.out.println("Token saved: " + token);
    }

    // Lấy token
    public static String getToken() {
        Map<String, String> data = readData();
        String token = data.getOrDefault("token", "");
        // System.out.println("Token loaded: " + token);
        return token;
    }

    // Xóa token (nhưng giữ lại loginStatus)
    public static void clearToken() {
        Map<String, String> data = readData();
        data.put("token", "");
        writeData(data);
        System.out.println("Token cleared.");
    }

    // Lưu trạng thái đăng nhập (giữ lại token nếu có)
    public static void setStatusLogin(String status) {
        Map<String, String> data = readData();
        data.put("loginStatus", status);
        writeData(data);
        // System.out.println("Login status saved: " + status);
    }

    // Lấy trạng thái đăng nhập
    public static String getStatusLogin() {
        Map<String, String> data = readData();
        String status = data.getOrDefault("loginStatus", "false");
        // System.out.println("Login status loaded: " + status);
        return status;
    }

    // hàm này sẽ được gọi khi người dùng đăng xuất
    public static void logout() {
        clearToken();
        setStatusLogin("false");
        System.out.println("User logged out.");
    }

    // Test nhanh
    public static void main(String[] args) {
        setStatusLogin("true");
        saveToken("abc123");
        String token = getToken();
        String status = getStatusLogin();
        System.out.println("Read token = " + token);
        System.out.println("Read login status = " + status);
        clearToken();
        System.out.println("After clear token = " + getToken());
    }
}
