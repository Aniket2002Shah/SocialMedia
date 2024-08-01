package com.example.socialmedia

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.socialmedia.dao.UserDao
import com.example.socialmedia.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUp_Activity : AppCompatActivity() {

    private val MY_CODE_REQUEST = 123
    private lateinit var googleSignInClient: GoogleSignInClient
    private val TAG = "SignInActivity Tag"
    private lateinit var auth: FirebaseAuth
    private lateinit var signInButton: SignInButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signInButton = findViewById<SignInButton>(R.id.signInButton)
        progressBar = findViewById<ProgressBar>(R.id.progress_Bar)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth

        signInButton.setOnClickListener {
            signUp()
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            val user = auth.currentUser!!
            updateUI(user)
        }
        catch (e:NullPointerException){
            Log.w(TAG,"NullPointerException in OnStart fun of class SignUp_Activity::class.java")
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityGettingResult(MY_CODE_REQUEST, result)
        }

    private fun signUp() {
        val signInIntent = googleSignInClient.signInIntent
        getContent.launch(signInIntent)
    }

    private fun onActivityGettingResult(myCodeRequest: Int, result: ActivityResult?) {
        if (result != null) {
            if ((result.resultCode == Activity.RESULT_OK) || (myCodeRequest == MY_CODE_REQUEST)) {
                val intent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                handleSignInResult(task)
            }
        }
    }


    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "Firebase GOOGLE authorisation succesfull with id =" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (apie: ApiException) {
            Log.w(
                TAG,
                "Firebase GOOGLE authorisation unsuccesfull with exception code =" + apie.statusCode
            )
        } catch (npe: NullPointerException) {
            Log.w(TAG, "Firebase GOOGLE authorisation unsuccesfull with NullPointerException")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        progressBar.visibility = View.VISIBLE
        signInButton.visibility = View.GONE

        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                updateUI(firebaseUser)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {

            firebaseUser?.let {
                val user = User(
                    it.uid.toString(),
                    it.displayName,
                    it.photoUrl.toString()
                )
                UserDao().addUser(user)
            }

        if (firebaseUser != null) {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        } else {
            progressBar.visibility = View.GONE
            signInButton.visibility = View.VISIBLE
        }
    }

}


















