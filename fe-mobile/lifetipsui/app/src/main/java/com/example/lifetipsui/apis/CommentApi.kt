package com.example.lifetipsui.apis
import com.example.lifetipsui.config.Config

object CommentApi {
    const val createCommentApi = "${Config.BASE_URL}/private/comment/create"
    const val getAllCommentByIdPostApi = "${Config.BASE_URL}/private/comment"
}