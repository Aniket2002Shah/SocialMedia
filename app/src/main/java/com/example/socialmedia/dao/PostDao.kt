package com.example.socialmedia.dao

import android.content.ContentValues.TAG
import android.util.Log
import com.example.socialmedia.model.Post
import com.example.socialmedia.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    private val db = FirebaseFirestore.getInstance()
    private val postCollection = db.collection("post")
    private val auth= Firebase.auth.currentUser
    private val userDao = UserDao()

    @OptIn(DelicateCoroutinesApi::class)
    fun createPost(text:String) {
        val id= auth!!.uid
        GlobalScope.launch (Dispatchers.IO){
            val user = userDao.getUserById(id).await().toObject(User::class.java)!!
            val timeStamp= System.currentTimeMillis()
            val post = Post(text,user,timeStamp)
            postCollection.document().set(post)
        }
      }

    fun getPostById(id:String): Task<DocumentSnapshot> {
        return postCollection.document(id).get()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateLikes(id: String){
        GlobalScope.launch(Dispatchers.IO) {
            val uid= auth!!.uid
            val post = getPostById(id).await().toObject(Post::class.java)!!
            val isliked = post.likedBy.contains(uid)
            if(isliked){
                post.likedBy.remove(uid)
            }
            else {
                post.likedBy.add(uid)
            }
            postCollection.document(id).set(post)
        }

    }

    fun getPostCollection() : CollectionReference{
        return postCollection
    }
}
