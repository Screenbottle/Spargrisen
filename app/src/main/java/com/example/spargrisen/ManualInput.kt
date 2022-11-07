package com.example.spargrisen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.spargrisen.fragments.HomeFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_manual_input.*

class ManualInput : AppCompatActivity() {
    lateinit var itemText : EditText
    lateinit var priceText: EditText
    lateinit var itemCategory: EditText
    lateinit var spinner: Spinner
    lateinit var calendarView: CalendarView
    lateinit var sendBtn : Button
    lateinit var checkBtn : Button
    var db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)

        val kategorival = resources.getStringArray(R.array.Kategorival)

        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, kategorival)
            spinner.adapter = adapter


            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
//                    Toast.makeText(this@ManualInput,
//                        getString(R.string.selected_item) + " " +
//                                "" + kategorival[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


        val itemText = findViewById<EditText>(R.id.itemText)
        val itemCategory = findViewById<EditText>(R.id.itemCategory)
        val priceText = findViewById<EditText>(R.id.priceText)
        val checkBtn = findViewById<Button>(R.id.checkBtn)
        val sendBtn = findViewById<Button>(R.id.sendBtn)
        val date = findViewById<CalendarView>(R.id.calendarView) // ändras
        //val kategorival = resources.getStringArray(R.array.Kategorival)

        checkBtn.setOnClickListener{
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)
        }
        sendBtn.setOnClickListener{
            val txtItemText = itemText.text.toString().trim()
            val txtPriceText = priceText.text.toString().trim()
            val txtItemCategory = itemCategory.text.toString().trim()
            val sdate = date.date.toString() // ändras
           // val skategorival = kategorival.size.toString() // list alt följer inte med till firebase

            val inputList = hashMapOf(

                "Item" to txtItemText,
                "Price" to txtPriceText,
                "itemCategory" to txtItemCategory,
               // "kategorival" to skategorival,// ändras visar sama siffra
                "Date" to sdate)

            db.collection("users").document(getUID()).collection("itemList").document().set(inputList)
                .addOnSuccessListener {
                    Log.d("DB", "Input added") // lägga till if empty

                   itemText.text.clear()
                     priceText.text.clear()
                    itemCategory.text.clear()

                    //val intent = Intent(this, HomeFragment::class.java)
                    //startActivity(intent)

                }.addOnFailureListener {
                    Log.d("DB", "Input failed")
                }


        }
        setContentView(R.layout.activity_manual_input)


        backToHomeBtn.setOnClickListener({startActivity(Intent(this, MainActivity::class.java)) })
    }
    fun getUID() : String {
        val auth = Firebase.auth

        val uid : String = auth.uid.toString()
        return uid
    }
}
