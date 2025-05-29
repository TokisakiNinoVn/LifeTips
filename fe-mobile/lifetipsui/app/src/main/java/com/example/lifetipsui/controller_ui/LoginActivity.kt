package com.example.lifetipsui.controller_ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifetipsui.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.appbar.MaterialToolbar
import androidx.lifecycle.lifecycleScope
import com.example.lifetipsui.MainActivity
import kotlinx.coroutines.launch
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

import com.example.lifetipsui.service.AuthService
import com.example.lifetipsui.service.StorageService

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        toolbar = findViewById(R.id.topAppBar)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = emailInput.text?.toString()?.trim() ?: ""
        val password = passwordInput.text?.toString()?.trim() ?: ""

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val response = AuthService.login(mapOf("email" to email, "password" to password))
            if (response == null) {
                Toast.makeText(this@LoginActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                return@launch
            }
            println("Response: $response")

            val status = response["status"]?.jsonPrimitive?.content ?: ""
            val message = response["message"]?.jsonPrimitive?.content ?: ""

            if (status == "success") {
                val token = response["token"]?.jsonPrimitive?.content ?: ""
                StorageService.saveToken(token)
                val dataObj = response["data"]?.jsonObject
                StorageService.setStatusLogin("true")
                Toast.makeText(this@LoginActivity, "Đăng nhập thành công: $message", Toast.LENGTH_LONG).show()
                // Chuyển hướng đến màn hình chính 
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            } else {
                Toast.makeText(this@LoginActivity, "Đăng nhập thất bại: $message", Toast.LENGTH_LONG).show()
            }
        }

    }

}