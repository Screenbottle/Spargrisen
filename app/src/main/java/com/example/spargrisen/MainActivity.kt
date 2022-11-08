package com.example.spargrisen


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.spargrisen.databinding.ActivityNavbarBinding
import com.example.spargrisen.fragments.CameraFragment
import com.example.spargrisen.fragments.GraphFragment
import com.example.spargrisen.fragments.HomeFragment
import com.example.spargrisen.fragments.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

var userID: Int = 0// Users ID

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        auth = Firebase.auth


        if (auth.currentUser == null) {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        } //else {




        setContentView(R.layout.activity_navbar)
            val viewbinding = ActivityNavbarBinding.inflate(layoutInflater)

            setContentView(viewbinding.root)
            replaceFragment(GraphFragment())
            viewbinding.bottomNavigation.setOnItemSelectedListener {

                when (it.itemId) {
                    R.id.ic_graph -> replaceFragment(GraphFragment())
                    R.id.ic_home -> replaceFragment(HomeFragment())
                    R.id.ic_settings -> replaceFragment(SettingsFragment())
                  //  R.id.ic_camera -> replaceFragment(CameraFragment())


                }
                true
            }


    }

    private fun replaceFragment(fragment: Fragment){
        if (fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }

    }
}

