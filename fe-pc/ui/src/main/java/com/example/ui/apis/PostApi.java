package com.example.ui.apis;

import static com.example.ui.constant.constant.BASE_URL;

public class PostApi {
    public static final String CREATEPOST_ENDPOINT = "/private/post/create-with-file";
    public static final String GETALLPOST_ENDPOINT = "/public/post/all";
    public static final String GETPOSTBYID_ENDPOINT = "/public/post/get/";
    public static final String DELETEPOST_ENDPOINT = "/public/post/delete/";
    public static final String UPDATEPOST_ENDPOINT = "/public/post/update/";

    public static String createPostEndpoint() {
        return BASE_URL + CREATEPOST_ENDPOINT;
    }

    public static String getAllPostEndpoint() {
        return BASE_URL + GETALLPOST_ENDPOINT;
    }

    public static String getPostByIdEndpoint(String postId) {
        return BASE_URL + GETPOSTBYID_ENDPOINT + postId;
    }

    public static String deletePostEndpoint(String postId) {
        return BASE_URL + DELETEPOST_ENDPOINT + postId;
    }

    public static String updatePostEndpoint(String postId) {
        return BASE_URL + UPDATEPOST_ENDPOINT + postId;
    }
}
