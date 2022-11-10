package com.example.spargrisen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.spargrisen.fragments.GraphFragment
import com.example.spargrisen.fragments.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    //lateinit var db : FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var emailText: EditText
    lateinit var userPassword1: EditText


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        emailText = findViewById(R.id.emailText)
        userPassword1 = findViewById(R.id.userPassword)


        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            logInUser()
        }



        val signupBtn = findViewById<Button>(R.id.signupBtn)
        signupBtn.setOnClickListener {
           goToSignupActivity()
        }

        if (auth.currentUser != null) {
            Log.d("!!!", "${auth.currentUser?.email}")
         goToMainpage()
        }
       // auth.signOut()
    }

    fun goToMainpage (){
        val intent = Intent(this, HomeFragment::class.java)
        startActivity(intent)
    }
    fun goToSignupActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun logInUser() {
        val email = emailText.text.toString()
        val password = userPassword1.text.toString()


        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("!!!", "Sign in sucess")
                    goToMainpage()

                } else {
                    Log.d("!!!", "Sign in fail ${task.exception}")
                    Toast.makeText(this, "Inloggning misslyckades.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }



}

