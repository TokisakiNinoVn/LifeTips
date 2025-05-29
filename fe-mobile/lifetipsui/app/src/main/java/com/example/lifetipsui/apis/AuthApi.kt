package com.example.lifetipsui.apis

import com.example.lifetipsui.config.Config

object AuthApi {
    const val loginApi = "${Config.BASE_URL}/public/auth/login"
    const val registerApi = "${Config.BASE_URL}/public/auth/register"
}
