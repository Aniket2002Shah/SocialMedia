package com.example.socialmedia

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.dao.PostDao
import com.example.socialmedia.dao.UserDao
import com.example.socialmedia.model.Post
import com.example.socialmedia.model.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity(),PostAdapter.OnPostItemClicked {

    private lateinit var adapter: PostAdapter
    private val auth = Firebase.auth

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.createPost)
        val sinOutButton= findViewById<Button>(R.id.signOut)
        val displayPic = findViewById<ImageView>(R.id.logo)
        val recylerView = findViewById<RecyclerView>(R.id.recylerView)

        val  postCollection= PostDao().getPostCollection()
        val query = postCollection.orderBy("createdAt",Query.Direction.DESCENDING)
        val  recylerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()

            recylerView.layoutManager = LinearLayoutManager(this)
            adapter = PostAdapter(recylerViewOptions,this)
            recylerView.adapter = adapter

        val uid= auth.uid!!
        GlobalScope.launch(Dispatchers.IO) {
           val user = UserDao().getUserById(uid).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main){
                Glide.with(displayPic.context).load(user.image).circleCrop()
                        .into(displayPic)
            }

        }

        floatingActionButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }


        sinOutButton.setOnClickListener {
           dialogBox()
        }
    }

    fun dialogBox(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialogTitle)
        builder.setMessage(R.string.diaologMessage)
        builder.setIcon(R.drawable.baseline_help_24)

        builder.setPositiveButton("Yes"){dialogInterface,which ->
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()
            Log.d(TAG,"GoogleSignInClient logout")

            auth.signOut()
            Log.d(TAG,"FirebaseAuth logout")

            startActivity(Intent(this,SignUp_Activity::class.java))
            finish()
            Log.d(TAG,"Clicked yes on Alert dialog box to sign out")
        }
        builder.setNegativeButton("Cancel"){dialogInterface, which ->
            Log.d(TAG,"Cancelled Alert dialog box to sign out")
        }

        val alertDialog= builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop(){
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikedButtonClicked(postId: String) {
     PostDao().updateLikes(postId)
    }

    override fun onShareButton(postId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val post = PostDao().getPostById(postId).await().toObject(Post::class.java)!!
            val data = post.description

            withContext(Dispatchers.Main) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "Hii see what what i found... :" +
                        " $data")
                val chooser = Intent.createChooser(intent, "SHARE this post.....")
                startActivity(chooser)
            }
        }
    }
}