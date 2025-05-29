package com.example.ui.apis;

import static com.example.ui.constant.constant.BASE_URL;

public class CategoryApi {
    public static final String GETALLCATEGORY_ENDPOINT = "/public/category/all";

    public static String getAllCategoryEndpoint() {
        return BASE_URL + GETALLCATEGORY_ENDPOINT;
    }
}
