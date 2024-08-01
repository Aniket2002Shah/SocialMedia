package com.example.socialmedia.dao

import android.content.ContentValues
import android.util.Log
import com.example.socialmedia.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    private val db = FirebaseFirestore.getInstance()
    private val userCollection:CollectionReference = db.collection("user")

    fun addUser(user: User?){
    user?.let {
        GlobalScope.launch(Dispatchers.IO) {
            userCollection.document(it.id).set(it)
        }
      }
    }

    fun getUserById(id:String): Task<DocumentSnapshot> {
        return userCollection.document(id).get()
    }

}