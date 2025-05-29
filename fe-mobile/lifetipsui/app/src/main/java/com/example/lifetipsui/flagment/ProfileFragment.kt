package com.example.lifetipsui.flagment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lifetipsui.R
import com.example.lifetipsui.controller_ui.LoginActivity
import com.example.lifetipsui.controller_ui.RegisterActivity
import com.example.lifetipsui.service.UserService
import com.example.lifetipsui.service.StorageService
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.test_button)
        val registerButton = view.findViewById<Button>(R.id.test_button2)
        val updateFullNameButton = view.findViewById<Button>(R.id.save_button)
        val logoutButton = view.findViewById<Button>(R.id.logout_button)

        // Lấy view email và display name
        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
        val displayNameInput = view.findViewById<TextInputEditText>(R.id.display_name_input)

        val isLogin = StorageService.getStatusLogin()
        if (isLogin == "true") {
            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        UserService.getInfor()
                    }

                    val code = response?.optInt("code", -1)
                    if (code == 200) {
                        val data = response.optJSONObject("data")
                        data?.let {
                            // Update UI với dữ liệu trả về
                            emailInput.setText(it.optString("email", ""))
                            displayNameInput.setText(it.optString("full_name", ""))
                        }
                    } else {
                        Toast.makeText(requireContext(), "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Error: ${e.message}")
                    Toast.makeText(requireContext(), "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            loginButton.visibility = View.GONE
            registerButton.visibility = View.GONE

            logoutButton.visibility = View.VISIBLE

            logoutButton.setOnClickListener {
                // Xử lý đăng xuất
                performLogout()
            }

        } else {
            logoutButton.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
            registerButton.visibility = View.VISIBLE
            emailInput.visibility = View.GONE
            displayNameInput.visibility = View.GONE
            updateFullNameButton.visibility = View.GONE

            loginButton.setOnClickListener {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }

            registerButton.setOnClickListener {
                val intent = Intent(requireContext(), RegisterActivity::class.java)
                startActivity(intent)
            }
        }

        updateFullNameButton.setOnClickListener {
            updateUserInfo()
        }
    }

    override fun onResume() {
        super.onResume()
        // Cập nhật lại giao diện khi fragment được hiển thị
        val isLogin = StorageService.getStatusLogin()
        if (isLogin == "true") {
            view?.findViewById<Button>(R.id.test_button)?.visibility = View.GONE
            view?.findViewById<Button>(R.id.test_button2)?.visibility = View.GONE
        } else {
            view?.findViewById<Button>(R.id.test_button)?.visibility = View.VISIBLE
            view?.findViewById<Button>(R.id.test_button2)?.visibility = View.VISIBLE
        }
    }

    private fun performLogout() {
        // Xóa trạng thái đăng nhập
        StorageService.setStatusLogin("false")

        // Xóa các dữ liệu người dùng nếu cần (có thể gọi StorageService để clear)
        StorageService.logout()

        Toast.makeText(requireContext(), "Bạn đã đăng xuất", Toast.LENGTH_SHORT).show()

        // Chuyển về màn hình Login
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // Hàm cập nhật thông tin người dùng
    private fun updateUserInfo() {
        val displayNameInput = view?.findViewById<TextInputEditText>(R.id.display_name_input)
        val fullName = displayNameInput?.text.toString().trim()

        if (fullName.isEmpty()) {
            Toast.makeText(requireContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val data = mapOf("fullName" to fullName)

                val response = withContext(Dispatchers.IO) {
                    UserService.updateInfor(data)
                }
                println("response: $response")

                val code = response?.optInt("code", -1) ?: -1
                if (code == 200) {
                    Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
                } else {
                    val msg = response?.optString("message", "Cập nhật thất bại") ?: "Cập nhật thất bại"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Lỗi cập nhật thông tin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
