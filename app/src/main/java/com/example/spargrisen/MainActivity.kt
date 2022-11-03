package com.example.spargrisen


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.spargrisen.databinding.ActivityGraphAndTableBinding
import com.example.spargrisen.fragments.CameraFragment
import com.example.spargrisen.fragments.GraphFragment
import com.example.spargrisen.fragments.HomeFragment
import com.example.spargrisen.fragments.SettingsFragment


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_graph_and_table)
        val viewbinding= ActivityGraphAndTableBinding.inflate(layoutInflater)

        setContentView(viewbinding.root)
        replaceFragment(GraphFragment())
        viewbinding.bottomNavigation.setOnItemSelectedListener {

            when(it.itemId){
                R.id.ic_graph ->replaceFragment(GraphFragment())
                R.id.ic_home ->replaceFragment(HomeFragment())
                R.id.ic_settings ->replaceFragment(SettingsFragment())
                R.id.ic_camera -> {
                    val intent = Intent(this, CameraActivity::class.java)
                    startActivity(intent)
                    replaceFragment(CameraFragment())
                }


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

