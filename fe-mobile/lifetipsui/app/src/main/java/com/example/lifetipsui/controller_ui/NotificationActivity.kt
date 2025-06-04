package com.example.lifetipsui.controller_ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetipsui.R
import com.example.lifetipsui.service.NotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_screen)

        // Set up back button
        findViewById<ImageView>(R.id.backIcon).setOnClickListener {
            finish()
        }

        // Initialize RecyclerView
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView)
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        notificationAdapter = NotificationAdapter(emptyList())
        notificationRecyclerView.adapter = notificationAdapter

        // Load notifications
        loadListNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadListNotification() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    NotificationService.getListNotificationService()
                }

                // Parse JSONObject từ response
                val jsonObject = JSONObject(response.toString())
                val jsonArray = jsonObject.getJSONArray("data")

                val notifications = mutableListOf<Notification>()
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    notifications.add(
                        Notification(
                            item.getInt("id"),
                            item.getString("title"),
                            item.getString("content"),
                            item.getString("created_at"),
                            item.getInt("post_id")
                        )
                    )
                }
                notificationAdapter.updateData(notifications)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    data class Notification(
        val id: Int,
        val title: String,
        val content: String,
        val createdAt: String,
        val postId: Int
    )

    // Adapter for RecyclerView
    inner class NotificationAdapter(private var notifications: List<Notification>) :
        RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

        inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView.findViewById(R.id.notificationTitle)
            val content: TextView = itemView.findViewById(R.id.notificationContent)

            init {
                itemView.setOnClickListener {
                    val notification = notifications[adapterPosition]
                    // Handle click event if needed
                     val intent = Intent(itemView.context, DetailActivity::class.java)
                     intent.putExtra("postId", notification.postId)
                     itemView.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_item, parent, false)
            return NotificationViewHolder(view)
        }

        override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
            val notification = notifications[position]
            holder.title.text = notification.title
            holder.content.text = notification.content.replace("\\n", "\n")
        }

        override fun getItemCount(): Int = notifications.size

        fun updateData(newNotifications: List<Notification>) {
            notifications = newNotifications
            notifyDataSetChanged()
        }
    }
}