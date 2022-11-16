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
    lateinit var currentKategori : String

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


                    currentKategori = getString(R.string.selected_item) + " " +
                            "" + kategorival[position]

                    //    Toast.makeText(this@ManualInput,
                    //       getString(R.string.selected_item) + " " +
                    //             "" + kategorival[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


        val itemText = findViewById<EditText>(R.id.itemText)
        val priceText = findViewById<EditText>(R.id.priceText)


        val checkBtn = findViewById<Button>(R.id.checkBtn)
        val sendBtn = findViewById<Button>(R.id.sendBtn)
        val date = findViewById<DatePicker>(R.id.calendarView)

        var purchaseDate: String = android.text.format.DateFormat.format("dd/MM/yyyy", Date()).toString() // Start with today's date

        val today = Calendar.getInstance()
        date.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, monthOfYear, dayOfMonth ->
            var month : String = (monthOfYear + 1).toString()
            var day = dayOfMonth.toString()

            if (monthOfYear < 9) {
                month = "0$month"
            }
            if(dayOfMonth < 10){
                day = "0$dayOfMonth"
            }

            purchaseDate = "$day/$month/$year"
            Log.d("Date", purchaseDate)
        }

        checkBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        sendBtn.setOnClickListener{
            if(itemText.text.isEmpty()  || priceText.text.isEmpty() ) {
                Toast.makeText(
                    this, "Fyll i alla fält.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else {

                val txtItemText = itemText.text.toString().trim()
                val txtPriceText = priceText.text.toString().toLong()
                val txtItemCategory = currentKategori

                DatabaseController().addInputData(txtItemText, txtPriceText, txtItemCategory, purchaseDate)

                priceText.text.clear()
                itemText.text.clear()

            }
        }

    }
    fun getUID() : String {
        val auth = Firebase.auth

        val uid : String = auth.uid.toString()
        return uid
    }
}