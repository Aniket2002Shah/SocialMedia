package com.example.socialmedia.model

data class Post(
    val description:String="",
    val createdBy:User=User(),
    val createdAt:Long=0,
    val likedBy:ArrayList<String> = ArrayList()
)