package com.example.spargrisen.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.spargrisen.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass.
 * Use the [AddInputTextFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddInputTextFragment : Fragment() {

    lateinit var itemText : EditText
    lateinit var priceText: EditText
    lateinit var itemCategory: EditText
    lateinit var spinner: Spinner
    lateinit var calendarView: CalendarView
    var db = Firebase.firestore

       // lateinit var bindinng : FragmentAddInputTextBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_manual_input, container, false)

        //val kategorival = resources.getStringArray(R.array.Kategorival)

        val checkBtn = view.findViewById<Button>(R.id.checkBtn)
        val sendBtn = view.findViewById<Button>(R.id.sendBtn)

        val itemText = view.findViewById<EditText>(R.id.ItemText)
        val itemCategory = view.findViewById<EditText>(R.id.itemCategory)
        val priceText = view.findViewById<EditText>(R.id.priceText)
        val date = view.findViewById<CalendarView>(R.id.calendarView)

       sendBtn.setOnClickListener{
           addInput()

       }

        return view
    }

    fun addInput() {

        val txtItemText = itemText.text.toString().trim()
        val txtPriceText = priceText.text.toString().trim()
        val txtItemCategory = itemCategory.text.toString().trim()

        //val spinner = spinner.text.toString().trim()

        val inputList = hashMapOf(

            "Item" to txtItemText,
            "Price" to txtPriceText,
            "itemCategory" to txtItemCategory,
            //"Date" to sdate
            )

        db.collection("users").document(getUID()).collection("itemList").document().set(inputList)
            .addOnSuccessListener {
                Log.d("DB", "Input added")
                //Homefragment
            }.addOnFailureListener {
                Log.d("DB", "Input failed")
                }
    }

            fun getUID() : String {
                val auth = Firebase.auth
                val uid : String = auth.uid.toString()
                return uid
            }
    }

