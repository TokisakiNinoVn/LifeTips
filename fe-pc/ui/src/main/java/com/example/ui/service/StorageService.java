package com.example.ui.service;

import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StorageService {
    private static final Gson gson = new Gson();

    // Safe base directory in user home folder
    private static final String BASE_PATH = System.getProperty("user.home") + File.separator + ".lifetips";
    private static final String FILE_PATH = BASE_PATH + File.separator + "localStorage.json";

    static {
        ensureFileExists();
    }

    private static void ensureFileExists() {
        try {
            File directory = new File(BASE_PATH);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("Created directory for local storage: " + BASE_PATH);
                }
            }

            File file = new File(FILE_PATH);
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("Created storage file: " + FILE_PATH);

                    Map<String, String> defaultData = new HashMap<>();
                    defaultData.put("token", "");
                    defaultData.put("loginStatus", "false");

                    try (Writer writer = new FileWriter(file)) {
                        gson.toJson(defaultData, writer);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error while creating storage file:");
            e.printStackTrace();
        }
    }

    private static Map<String, String> readData() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Map<?, ?> raw = gson.fromJson(reader, Map.class);
            Map<String, String> data = new HashMap<>();
            if (raw != null) {
                raw.forEach((k, v) -> data.put(String.valueOf(k), String.valueOf(v)));
            }
            return data;
        } catch (IOException e) {
            System.err.println("Error while reading from storage file:");
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private static void writeData(Map<String, String> data) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Error while writing to storage file:");
            e.printStackTrace();
        }
    }

    public static void saveToken(String token) {
        Map<String, String> data = readData();
        data.put("token", token);
        writeData(data);
    }

    public static String getToken() {
        return readData().getOrDefault("token", "");
    }

    public static void clearToken() {
        Map<String, String> data = readData();
        data.put("token", "");
        writeData(data);
        System.out.println("Token cleared.");
    }

    public static void setStatusLogin(String status) {
        Map<String, String> data = readData();
        data.put("loginStatus", status);
        writeData(data);
    }

    public static String getStatusLogin() {
        return readData().getOrDefault("loginStatus", "false");
    }

    public static void logout() {
        clearToken();
        setStatusLogin("false");
        System.out.println("User logged out.");
    }
}
