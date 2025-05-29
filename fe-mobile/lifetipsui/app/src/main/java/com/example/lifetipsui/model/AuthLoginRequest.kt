@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.lifetipsui.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthLoginRequest(
    val email: String,
    val password: String
)
