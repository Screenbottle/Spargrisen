package com.example.spargrisen.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spargrisen.LoginActivity
import com.example.spargrisen.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_settings, container, false,)
        // Inflate the layout for this fragment



        view.logOutBtn.setOnClickListener{

            Firebase.auth.signOut()

            goToLogin()
        }
        return view

    }


    fun goToLogin (){
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

    companion object{

    }



}