package com.example.lifetipsui.controller_ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lifetipsui.R
import com.example.lifetipsui.service.AuthService
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import android.content.Intent
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var nameInput: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var linkLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_screen)

        // Gắn các view
        emailInput = findViewById(R.id.email_edit_text)
        passwordInput = findViewById(R.id.password_edit_text)
        confirmPasswordInput = findViewById(R.id.confirm_password_edit_text)
        nameInput = findViewById(R.id.name_edit_text)

        registerButton = findViewById(R.id.register_button)
        linkLogin = findViewById(R.id.login_link)

        // Khi người dùng nhấn đăng ký
        registerButton.setOnClickListener {
            register()
        }

        // Khi người dùng muốn quay lại đăng nhập
        linkLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun register() {
        val email = emailInput.text?.toString()?.trim() ?: ""
        val password = passwordInput.text?.toString()?.trim() ?: ""
        val confirmPassword = confirmPasswordInput.text?.toString()?.trim() ?: ""
        val name = nameInput.text?.toString()?.trim() ?: ""

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = AuthService.register(
                    mapOf("email" to email, "password" to password, "fullname" to name)
                )
                println("Response: $response")
                // Nếu response thành công (giả sử AuthService trả về true hoặc gì đó)
                Toast.makeText(this@RegisterActivity, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Đăng ký thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
