package com.example.spargrisen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spargrisen.fragments.GraphFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var email : EditText
    lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

         val loginTv = findViewById<TextView>(R.id.loginTV)
        loginTv.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Auth
        auth = Firebase.auth

        val registerBtn = findViewById<Button>(R.id.signupBtn)
        registerBtn.setOnClickListener {
            registerUser()
        }

        //Initialize Firestore
        db = FirebaseFirestore.getInstance()

    }

    fun registerUser() {

        val email = findViewById<EditText>(R.id.emailText)
        val password = findViewById<EditText>(R.id.userPassword)
        val fullname = findViewById<EditText>(R.id.fullName)

        if(email.text.isEmpty() || password.text.isEmpty() || fullname.text.isEmpty() ){
            Toast.makeText(this, "Fyll i alla fält.",
                Toast.LENGTH_SHORT).show()
            return
        }
        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()
        val inputFullname = fullname.text.toString()

        auth.createUserWithEmailAndPassword(inputEmail,inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    DatabaseController().registerUserToFirestore(inputFullname, inputEmail) // Also register user to Firestore

                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    //val user = auth.currentUser
                    Toast.makeText(baseContext, "Du är nu inloggad.",
                        Toast.LENGTH_SHORT).show()

                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Error inträffa ${it.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}