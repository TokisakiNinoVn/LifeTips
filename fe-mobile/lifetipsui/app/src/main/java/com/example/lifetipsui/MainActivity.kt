package com.example.lifetipsui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.lifetipsui.controller_ui.AddActivity
import com.example.lifetipsui.controller_ui.NotificationActivity
import com.example.lifetipsui.flagment.SettingsFragment
import com.example.lifetipsui.flagment.HomeFragment
import com.example.lifetipsui.flagment.ProfileFragment
import com.example.lifetipsui.flagment.SavedFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var notificationView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        // Áp padding cho notch
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNav = findViewById(R.id.bottomNav)
        notificationView = findViewById(R.id.notificationscreen)

        // Load mặc định
        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_explore -> loadFragment(SettingsFragment())
                R.id.nav_add -> {
                    val intent = Intent(this, AddActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_saved -> loadFragment(SavedFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }

        // Xử lý sự kiện click cho notificationView chuyển qua màn hình thông báo
        notificationView.setOnClickListener {
            // Chuyển hướng
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
