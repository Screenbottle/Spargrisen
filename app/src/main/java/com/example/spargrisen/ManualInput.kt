package com.example.spargrisen

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.spargrisen.fragments.GraphFragment
import com.example.spargrisen.fragments.HomeFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.Date
import java.util.*

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




        val itemText = findViewById<EditText>(R.id.itemText)
        val itemCategory = findViewById<EditText>(R.id.itemCategory)
        val priceText = findViewById<EditText>(R.id.priceText)


        val checkBtn = findViewById<Button>(R.id.checkBtn)
        val sendBtn = findViewById<Button>(R.id.sendBtn)
        val date = findViewById<DatePicker>(R.id.calendarView)

        var purchaseDate: String = "" ?: "No date"

        val today = Calendar.getInstance()
        date.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, monthOfYear, dayOfMonth ->
            val month = monthOfYear + 1
            purchaseDate = "$dayOfMonth/$month/$year"
            Log.d("Date", purchaseDate)
        }

        checkBtn.setOnClickListener{
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)
        }

        sendBtn.setOnClickListener{
            if(itemText.text.isEmpty() || itemCategory.text.isEmpty() || priceText.text.isEmpty() ) {
                Toast.makeText(
                    this, "Fyll i alla fält.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else {

                val txtItemText = itemText.text.toString().trim()
                val txtPriceText = priceText.text.toString().toLong()
                val txtItemCategory = itemCategory.text.toString().trim()

                DatabaseController().addInputData(txtItemText, txtPriceText, txtItemCategory, purchaseDate)

            }
        }

    }
    fun getUID() : String {
        val auth = Firebase.auth

        val uid : String = auth.uid.toString()
        return uid
    }
}