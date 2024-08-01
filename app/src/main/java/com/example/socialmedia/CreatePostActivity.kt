package com.example.socialmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.socialmedia.dao.PostDao

class CreatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        val submitPost= findViewById<Button>(R.id.submitPost)
        val writePost = findViewById<EditText>(R.id.writePost)

        submitPost.setOnClickListener {
            val text= writePost.text.toString().trim()
            if(text.isNotEmpty()){
                PostDao().createPost(text)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}