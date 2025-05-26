//file: AuthApi.java
package com.example.ui.apis;

import static com.example.ui.constant.constant.BASE_URL;

public class AuthApi {
    // Định nghĩa các phương thức liên quan đến xác thực người dùng
    public static final String LOGIN_ENDPOINT = "/public/auth/login";
    public static final String REGISTER_ENDPOINT = "/public/auth/register";
    public static final String LOGOUT_ENDPOINT = "/public/auth/logout";

    // Các phương thức khác có thể được thêm vào sau này
    public static String getLoginUrl() {
        return BASE_URL + LOGIN_ENDPOINT;
    }

    public static String getRegisterUrl() {
        return BASE_URL + REGISTER_ENDPOINT;
    }

    public static String getLogoutUrl() {
        return BASE_URL + LOGOUT_ENDPOINT;
    }

}
