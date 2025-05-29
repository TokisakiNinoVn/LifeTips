package com.example.lifetipsui.apis
import com.example.lifetipsui.config.Config

object PostApi {
    const val createPostApi = "${Config.BASE_URL}/private/post/create-with-file"
    const val savePostApi = "${Config.BASE_URL}/private/post/save"
    const val unsavePostApi = "${Config.BASE_URL}/private/post/a/saved"
    const val getAllByUserPostApi = "${Config.BASE_URL}/private/post/c/user"
    const val getAllSavePostApi = "${Config.BASE_URL}/private/post/d/saved"
    const val updatePostApi = "${Config.BASE_URL}/private/post/update"
    const val deletePostApi = "${Config.BASE_URL}/private/post/delete"


    const val searchPostApi = "${Config.BASE_URL}/public/post/e/search"
    const val getByIdPostApi = "${Config.BASE_URL}/public/post"
    const val getRandomPostApi = "${Config.BASE_URL}/public/post/a/random"
    const val getSameCategoryPostApi = "${Config.BASE_URL}/public/post/b/same-type/"
}