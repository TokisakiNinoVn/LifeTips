package com.example.ui.service;

import com.example.ui.apis.UserApi;
import com.example.ui.config.ApiMethodsPrivate;
import org.json.JSONObject;

public class UserService {
    // Load thông tin
    public static JSONObject getInforUserService() {
        String url = UserApi.getInforUserApi;
        return ApiMethodsPrivate.getRequest(url);
    }
    
    // update thông tin
    public static JSONObject updateUserService(JSONObject body) {
        String url = UserApi.updateInforUserApi;
        return ApiMethodsPrivate.putRequest(url, body);
    }

}
