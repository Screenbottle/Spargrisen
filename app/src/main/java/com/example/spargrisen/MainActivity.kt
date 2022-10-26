package com.example.spargrisen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.spargrisen.fragments.GraphFragment
import com.example.spargrisen.fragments.HomeFragment
import com.example.spargrisen.fragments.SettingsFragment
import com.example.spargrisen.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.jjoe64.graphview.GraphView
import kotlinx.android.synthetic.main.activity_graph_and_table.*
import kotlinx.android.synthetic.main.activity_graph_and_table.view.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewbinding= ActivityMainBinding.inflate(layoutInflater)

//        setContentView(viewbinding.root)
//        replaceFragment(GraphFragment())


//        when(it.itemId){
//                R.id.ic_baseline_auto_graph_24 ->replaceFragment(GraphFragment())
//                R.id.ic_baseline_home_24 ->replaceFragment(HomeFragment())
//                R.id.ic_baseline_settings_24 ->replaceFragment(SettingsFragment())
//
//            }
//            true
//        }
//
//    }
//
//
//    private fun replaceFragment(fragment: Fragment){
//        if (fragment != null){
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_container, fragment)
//            transaction.commit()
//        }
//
    }

}

