package com.example.spargrisen

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.type.Date
import com.google.type.DateTime
import java.text.SimpleDateFormat


class DatabaseController {

    fun getUID() : String {
        val auth = Firebase.auth

        val uid : String = auth.uid.toString()
        return uid
    }

    fun getFullName(uid : String) : String? {
        val db = FirebaseFirestore.getInstance()
        var fullName : String? = ""

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                fullName = document.getString("fName")
                Log.d("DB", fullName.toString())
            }

        return fullName
    }

    fun getEmail(uid : String) : String? {
        val db = FirebaseFirestore.getInstance()
        var email : String? = ""
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                email = document.getString("email")
                Log.d("DB", email.toString())
            }
        return email
    }

    fun registerUserToFirestore(inputFullname : String, inputEmail : String) {
            val db = FirebaseFirestore.getInstance()
            val auth = Firebase.auth

            //Add data to firestore db
            val user: MutableMap<String, Any> = HashMap()
            user["fName"] = inputFullname
            user["email"] = inputEmail

            val documentReference = db.collection("users").document(getUID())

            documentReference.set(user).addOnSuccessListener {
                Log.d("DB", "onSuccess: user Profile created: $userID")
            }.addOnFailureListener { e -> Log.d("DB", "onFailure: $e") }
    }

    // Use this to add input to the database
    fun addInputData(purchaseName : String, purchaseAmount : Int, purchaseCost : Long, purchaseDate : String) { // purchaseDate = "DD/MM/YEAR"
        val db = FirebaseFirestore.getInstance()

        val purchaseDateToMillis = convertDateToMillis(purchaseDate) // Convert timestamp to millis so we can query it in firestore

        val inputData: MutableMap<Any, Any> = HashMap()
        inputData["purchaseName"] = purchaseName
        inputData["purchaseAmount"] = purchaseAmount
        inputData["purchaseCost"] = purchaseCost
        inputData["purchaseDate"] = purchaseDateToMillis!!
        inputData["purchaseDateString"] = purchaseDate

        val inputRef = db.collection("users").document(getUID()).collection("values").document()

        inputRef.set(inputData).addOnSuccessListener {
            Log.d("DB", "Input added")
           // Log.d("DB", purchaseDateToMillis.toString())
        }.addOnFailureListener {
            Log.d("DB", "Input failed")
        }
    }

    fun totalCostThisYear() {

    }

    fun convertDateToMillis (date : String) : Long? {
        val simpleDate = SimpleDateFormat("dd/M/yyyy")

        val mDate: java.util.Date? = simpleDate.parse(date)
        val timeInMilliseconds : Long? = mDate?.time

        Log.d("DB", timeInMilliseconds.toString())

        return timeInMilliseconds
    }



}