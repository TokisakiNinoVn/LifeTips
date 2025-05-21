package com.example.lifetipsui.controller_ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lifetipsui.R
import com.google.android.material.appbar.MaterialToolbar

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_post)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}
