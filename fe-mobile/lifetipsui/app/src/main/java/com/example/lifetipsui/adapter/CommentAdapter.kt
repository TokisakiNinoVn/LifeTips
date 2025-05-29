package com.example.lifetipsui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetipsui.R
import com.example.lifetipsui.model.Comment

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textAuthor: TextView = view.findViewById(R.id.textCommentAuthor)
        val textContent: TextView = view.findViewById(R.id.textCommentContent)
        val textDate: TextView = view.findViewById(R.id.textCommentDate)
        val rateComment: TextView = view.findViewById(R.id.rateComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.textAuthor.text = comment.fullName
        holder.textContent.text = comment.content
        holder.textDate.text = comment.createdAt
        holder.rateComment.text = comment.rate.toString()
    }

    override fun getItemCount(): Int = comments.size
}