package com.example.spargrisen.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spargrisen.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.category_list.*
import com.example.spargrisen.DatabaseController
import com.example.spargrisen.MyAdapter
import com.google.firebase.firestore.ktx.firestore
import kotlinx.android.synthetic.main.activity_manual_input.*
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {


    lateinit var  recyclerView: RecyclerView
    lateinit var inputList : ArrayList<InputText>
    lateinit var adapter : MyAdapter
    var db = Firebase.firestore



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_home, container, false,)

        var dbController = DatabaseController()

        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        inputList = arrayListOf()

        val addInputBtn = view.findViewById<Button>(R.id.addInputBtn)

        addInputBtn.setOnClickListener {
            val intent = Intent(requireContext(), AddInputTextFragment::class.java)
        }

       // recyclerView1.adapter


        db = FirebaseFirestore.getInstance()

        db.collection("users").document(getUID()).collection("values").get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (data in it.documents){
                        val inputText : InputText? = data.toObject(InputText ::class.java)
                        if (inputText != null) {
                            inputList.add(inputText)
                        }
                    }
                    recyclerView.adapter = MyAdapter(inputList)
                }
            }



        return view
    }


    fun getUID() : String {
        val auth = Firebase.auth

        val uid : String = auth.uid.toString()
        return uid
    }

fun fetchData() {




}


}


