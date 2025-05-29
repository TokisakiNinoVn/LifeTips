package com.example.lifetipsui.flagment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.lifetipsui.R

class DetailsPost : Fragment(R.layout.flagment_detail_post) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.flagment_detail_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val postId = arguments?.getString("postId")
//        view.findViewById<TextView>(R.id.textPostId)?.text = "Post ID: $postId"
    }
}