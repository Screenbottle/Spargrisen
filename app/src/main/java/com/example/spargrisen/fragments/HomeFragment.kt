package com.example.spargrisen.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spargrisen.DatabaseController
import com.example.spargrisen.MyAdapter
import com.example.spargrisen.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_manual_input.*
import kotlinx.android.synthetic.main.category_list.*
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
    var isOpen = false


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fabOpen = AnimationUtils.loadAnimation(requireContext(),R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(requireContext(),R.anim.fab_close)
        val fabRClockwise = AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_clockwise)
        val fabRAntiClockwise = AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_anticlockwise)


        var view = inflater.inflate(R.layout.fragment_home, container, false,)

        var dbController = DatabaseController()

        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        inputList = arrayListOf()

        val addInputBtn = view.findViewById<FloatingActionButton>(R.id.addInputBtn)

        addInputBtn.setOnClickListener {
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            startActivity(intent)
            if (isOpen){

                addManualInputBtn.startAnimation(fabClose)
                addInputCameraBtn.startAnimation(fabClose)
                addInputBtn.startAnimation(fabRClockwise)

                isOpen= false
            }

            else{
                addManualInputBtn.startAnimation(fabOpen)
                addInputCameraBtn.startAnimation(fabOpen)
                addInputBtn.startAnimation(fabRAntiClockwise)

                addManualInputBtn.isClickable
                addInputCameraBtn.isClickable

                isOpen = true

            }

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


