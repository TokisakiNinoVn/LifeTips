package com.example.ui.apis;

import static com.example.ui.constant.constant.BASE_URL;

public class PostApi {
    public static final String CREATEPOST_ENDPOINT = "/private/post/create-with-file";
    public static final String GETALLPOST_ENDPOINT = BASE_URL + "/public/post/f/all";
    public static final String GETPOSTBYID_ENDPOINT = BASE_URL + "/public/post";
    public static final String DELETEPOST_ENDPOINT = BASE_URL + "/private/post/delete";
    public static final String UPDATEPOST_ENDPOINT = BASE_URL + "/private/post/update";
    
    public static final String savePostApi = BASE_URL + "/private/post/save";
    public static final String getAllPostUserApi = BASE_URL + "/private/post/c/user";
    public static final String getAllSavePostApi = BASE_URL + "/private/post/d/saved";
    public static final String unavePostApi = BASE_URL + "/private/post/a/saved";
    public static final String getAllNotificationApi = BASE_URL + "/private/notification";

    public static final String createCommentPostApi = BASE_URL + "/private/comment/create";

    public static String createPostEndpoint() {
        return BASE_URL + CREATEPOST_ENDPOINT;
    }
}
