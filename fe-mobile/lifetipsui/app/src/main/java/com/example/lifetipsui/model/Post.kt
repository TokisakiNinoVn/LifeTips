package com.example.lifetipsui.model

import java.io.File

data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val user: User,
    val category: Category?,
    val files: List<PostFile>
)

data class PostFile(
    val id: Int,
    val url: String
)

data class User(
    val full_name: String,
    val avatar: String
)

data class PostResponse(
    val code: Int,
    val data: List<Post>
)
data class Category(
    val id: Int,
    val name: String,
    val description: String
)