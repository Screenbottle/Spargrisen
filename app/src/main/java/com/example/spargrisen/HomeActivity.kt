package com.example.spargrisen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.spargrisen.databinding.ActivityHomeBinding
import com.example.spargrisen.fragments.GraphFragment
import com.example.spargrisen.fragments.HomeFragment
import com.example.spargrisen.fragments.SettingsFragment

class HomeActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewbinding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(viewbinding.root)
        replaceFragment(HomeFragment())
        viewbinding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.ic_graph -> replaceFragment(GraphFragment())
                R.id.ic_home -> replaceFragment(HomeFragment())
                R.id.ic_settings -> replaceFragment(SettingsFragment())


            }
            true
        }


    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }

    }

}